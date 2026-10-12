import { CircleAlert, Salad } from 'lucide-react'
import { DietPlanCard } from '@/features/diet-plans/DietPlanCard'
import { useDietPlans } from '@/features/diet-plans/useDietPlans'

const gridClassName = 'grid gap-6 sm:grid-cols-2 lg:grid-cols-3'

export function DietPlanList() {
  const { data: dietPlans, isPending, isError, refetch } = useDietPlans()

  if (isPending) {
    return (
      <ul aria-busy="true" aria-label="Ładowanie diet" className={gridClassName}>
        {[1, 2, 3].map((placeholder) => (
          <li key={placeholder} className="h-44 animate-pulse rounded-2xl bg-slate-100" />
        ))}
      </ul>
    )
  }

  if (isError) {
    return (
      <div
        role="alert"
        className="flex items-start gap-3 rounded-2xl border border-red-200 bg-red-50 p-6 text-red-800"
      >
        <CircleAlert aria-hidden className="mt-0.5 size-5 shrink-0" />
        <div>
          <p>Nie udało się pobrać listy diet.</p>
          <button
            type="button"
            onClick={() => void refetch()}
            className="mt-2 font-medium underline hover:no-underline"
          >
            Spróbuj ponownie
          </button>
        </div>
      </div>
    )
  }

  if (dietPlans.length === 0) {
    return (
      <div className="flex flex-col items-center rounded-2xl border border-dashed border-slate-300 px-6 py-12 text-center">
        <Salad aria-hidden className="size-8 text-brand" />
        <p className="mt-3 text-slate-600">Wkrótce pojawią się tutaj nasze diety.</p>
      </div>
    )
  }

  return (
    <ul className={gridClassName}>
      {dietPlans.map((dietPlan, index) => (
        <li key={dietPlan.id}>
          <DietPlanCard dietPlan={dietPlan} index={index} />
        </li>
      ))}
    </ul>
  )
}
