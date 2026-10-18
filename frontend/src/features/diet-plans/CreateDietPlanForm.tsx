import { zodResolver } from '@hookform/resolvers/zod'
import { CircleAlert, CircleCheck, Plus } from 'lucide-react'
import { useId, useState } from 'react'
import { useForm, type UseFormSetError } from 'react-hook-form'
import { ApiError } from '@/api/apiError'
import {
  createDietPlanSchema,
  type CreateDietPlanValues,
} from '@/features/diet-plans/createDietPlanSchema'
import { useCreateDietPlan } from '@/features/diet-plans/useCreateDietPlan'

const formFields: ReadonlyArray<string> = ['name', 'kcal'] satisfies Array<
  keyof CreateDietPlanValues
>

function isFormField(field: string): field is keyof CreateDietPlanValues {
  return formFields.includes(field)
}

function showServerErrors(error: unknown, setError: UseFormSetError<CreateDietPlanValues>) {
  const fieldErrors = error instanceof ApiError ? error.fieldErrors : []
  let shownFieldError = false
  for (const { field, message } of fieldErrors) {
    if (isFormField(field)) {
      setError(field, { type: 'server', message }, { shouldFocus: !shownFieldError })
      shownFieldError = true
    }
  }
  if (shownFieldError) {
    return
  }

  const message =
    error instanceof ApiError && error.status === 400
      ? 'Sprawdź poprawność danych i spróbuj ponownie.'
      : 'Nie udało się zapisać diety. Spróbuj ponownie.'
  setError('root.server', { type: 'server', message })
}

const inputClassName =
  'mt-1.5 block w-full rounded-lg border border-slate-300 px-3 py-2 shadow-sm transition outline-none focus:border-brand focus:ring-2 focus:ring-brand-muted aria-invalid:border-red-400 aria-invalid:focus:ring-red-200'

export function CreateDietPlanForm() {
  const fieldId = useId()
  const createDietPlan = useCreateDietPlan()
  const [createdDietName, setCreatedDietName] = useState<string | null>(null)
  const {
    register,
    handleSubmit,
    reset,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(createDietPlanSchema),
    defaultValues: { name: '' },
  })

  async function onSubmit(values: CreateDietPlanValues) {
    setCreatedDietName(null)
    try {
      const created = await createDietPlan.mutateAsync(values)
      reset()
      setCreatedDietName(created.name)
    } catch (error) {
      showServerErrors(error, setError)
    }
  }

  const titleId = `${fieldId}-title`
  const nameId = `${fieldId}-name`
  const kcalId = `${fieldId}-kcal`

  return (
    <form
      noValidate
      aria-labelledby={titleId}
      onSubmit={handleSubmit(onSubmit)}
      className="rounded-2xl bg-white p-6 ring-1 ring-slate-200"
    >
      <h2 id={titleId} className="text-lg font-semibold">
        Nowa dieta
      </h2>

      <div className="mt-5">
        <label htmlFor={nameId} className="text-sm font-medium text-slate-700">
          Nazwa
        </label>
        <input
          id={nameId}
          type="text"
          autoComplete="off"
          placeholder="np. Keto"
          aria-invalid={errors.name ? true : undefined}
          aria-describedby={errors.name ? `${nameId}-error` : undefined}
          className={inputClassName}
          {...register('name')}
        />
        {errors.name && (
          <p id={`${nameId}-error`} className="mt-1.5 text-sm text-red-700">
            {errors.name.message}
          </p>
        )}
      </div>

      <div className="mt-4">
        <label htmlFor={kcalId} className="text-sm font-medium text-slate-700">
          Kaloryczność (kcal dziennie)
        </label>
        <input
          id={kcalId}
          type="number"
          inputMode="numeric"
          min={1}
          step={1}
          placeholder="np. 2000"
          aria-invalid={errors.kcal ? true : undefined}
          aria-describedby={errors.kcal ? `${kcalId}-error` : undefined}
          className={inputClassName}
          {...register('kcal', { valueAsNumber: true })}
        />
        {errors.kcal && (
          <p id={`${kcalId}-error`} className="mt-1.5 text-sm text-red-700">
            {errors.kcal.message}
          </p>
        )}
      </div>

      {errors.root?.server && (
        <p
          role="alert"
          className="mt-5 flex items-start gap-2 rounded-lg bg-red-50 p-3 text-sm text-red-800"
        >
          <CircleAlert aria-hidden className="mt-0.5 size-4 shrink-0" />
          {errors.root.server.message}
        </p>
      )}

      {createdDietName && (
        <p
          role="status"
          className="mt-5 flex items-start gap-2 rounded-lg bg-brand-soft p-3 text-sm text-brand-strong"
        >
          <CircleCheck aria-hidden className="mt-0.5 size-4 shrink-0" />
          Dodano dietę „{createdDietName}”.
        </p>
      )}

      <button
        type="submit"
        disabled={isSubmitting}
        className="mt-6 inline-flex w-full items-center justify-center gap-2 rounded-full bg-brand px-4 py-2.5 font-medium text-brand-foreground transition hover:bg-brand-strong disabled:cursor-wait disabled:opacity-60"
      >
        <Plus aria-hidden className="size-4" />
        {isSubmitting ? 'Zapisywanie…' : 'Dodaj dietę'}
      </button>
    </form>
  )
}
