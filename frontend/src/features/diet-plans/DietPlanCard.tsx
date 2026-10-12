import { Flame } from 'lucide-react'
import type { DietPlan } from '@/features/diet-plans/dietPlansApi'

const accents = [
  'from-emerald-400 to-teal-500',
  'from-amber-300 to-orange-400',
  'from-sky-400 to-indigo-400',
  'from-rose-300 to-pink-400',
  'from-lime-300 to-green-500',
]

interface DietPlanCardProps {
  dietPlan: DietPlan
  index: number
}

export function DietPlanCard({ dietPlan, index }: DietPlanCardProps) {
  const accent = accents[index % accents.length]

  return (
    <article className="group flex h-full flex-col overflow-hidden rounded-2xl bg-white ring-1 ring-slate-200 transition hover:-translate-y-1 hover:shadow-lg motion-reduce:hover:translate-y-0">
      <div aria-hidden className={`h-2 bg-gradient-to-r ${accent}`} />
      <div className="flex flex-1 flex-col p-6">
        <h3 className="text-xl font-semibold">{dietPlan.name}</h3>
        <p className="mt-3 inline-flex items-center gap-2 text-slate-600">
          <Flame aria-hidden className="size-4 text-orange-500" />
          <span>
            <span className="font-semibold text-slate-900">{dietPlan.kcal}</span> kcal dziennie
          </span>
        </p>
        <button
          type="button"
          disabled
          title="Zamawianie będzie dostępne wkrótce"
          className="mt-8 rounded-full bg-brand px-4 py-2.5 font-medium text-brand-foreground transition disabled:cursor-not-allowed disabled:opacity-50"
        >
          Zamów wkrótce
        </button>
      </div>
    </article>
  )
}
