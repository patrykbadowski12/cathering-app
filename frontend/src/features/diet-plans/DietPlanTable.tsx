import { CircleAlert } from 'lucide-react'
import type { DietPlanState } from '@/features/diet-plans/dietPlansApi'
import { useAllDietPlans } from '@/features/diet-plans/useDietPlans'

const stateBadges: Record<DietPlanState, { label: string; className: string }> = {
  ACTIVE: { label: 'Aktywna', className: 'bg-brand-soft text-brand-strong' },
  INACTIVE: { label: 'Nieaktywna', className: 'bg-slate-100 text-slate-600' },
}

export function DietPlanTable() {
  const { data: dietPlans, isPending, isError, refetch } = useAllDietPlans()

  if (isPending) {
    return (
      <div aria-busy="true" aria-label="Ładowanie diet" className="space-y-2">
        {[1, 2, 3].map((placeholder) => (
          <div key={placeholder} className="h-12 animate-pulse rounded-lg bg-slate-200/60" />
        ))}
      </div>
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
      <p className="rounded-2xl border border-dashed border-slate-300 bg-white px-6 py-12 text-center text-slate-600">
        Nie masz jeszcze żadnej diety. Dodaj pierwszą w formularzu.
      </p>
    )
  }

  return (
    <div className="overflow-x-auto rounded-2xl bg-white ring-1 ring-slate-200">
      <table className="w-full text-left text-sm">
        <caption className="sr-only">Diety</caption>
        <thead className="border-b border-slate-200 bg-slate-50 text-slate-500">
          <tr>
            <th scope="col" className="px-4 py-3 font-medium">
              Nazwa
            </th>
            <th scope="col" className="px-4 py-3 text-right font-medium">
              Kcal dziennie
            </th>
            <th scope="col" className="px-4 py-3 font-medium">
              Status
            </th>
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-100">
          {dietPlans.map((dietPlan) => {
            const badge = stateBadges[dietPlan.state]
            return (
              <tr key={dietPlan.id}>
                <td className="px-4 py-3 font-medium text-slate-900">{dietPlan.name}</td>
                <td className="px-4 py-3 text-right tabular-nums">{dietPlan.kcal}</td>
                <td className="px-4 py-3">
                  <span
                    className={`inline-flex rounded-full px-2.5 py-0.5 text-xs font-medium ${badge.className}`}
                  >
                    {badge.label}
                  </span>
                </td>
              </tr>
            )
          })}
        </tbody>
      </table>
    </div>
  )
}
