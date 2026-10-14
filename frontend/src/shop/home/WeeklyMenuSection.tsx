import { useEffect, useRef } from 'react'
import { useLocation, useSearchParams } from 'react-router'
import { useDietPlans } from '@/features/diet-plans/useDietPlans'
import { DIET_SEARCH_PARAM, MENU_SECTION_ID } from '@/features/menu/menuLink'
import { WeeklyMenu } from '@/features/menu/WeeklyMenu'

export function WeeklyMenuSection() {
  const { data: dietPlans, isPending: dietPlansPending } = useDietPlans()
  const [searchParams, setSearchParams] = useSearchParams()
  const location = useLocation()
  const sectionRef = useRef<HTMLElement>(null)

  const selectedDietId = searchParams.get(DIET_SEARCH_PARAM)
  const selectedDiet =
    dietPlans?.find((dietPlan) => dietPlan.id === selectedDietId) ?? dietPlans?.[0]

  useEffect(() => {
    if (!dietPlansPending && location.hash === `#${MENU_SECTION_ID}`) {
      sectionRef.current?.scrollIntoView()
    }
  }, [location.key, location.hash, dietPlansPending])

  function selectDiet(dietId: string) {
    setSearchParams(
      (params) => {
        params.set(DIET_SEARCH_PARAM, dietId)
        return params
      },
      { replace: true, preventScrollReset: true },
    )
  }

  return (
    <section ref={sectionRef} id={MENU_SECTION_ID} className="border-t border-slate-200">
      <div className="mx-auto max-w-6xl px-6 py-16">
        <div className="max-w-2xl">
          <h2 className="text-3xl font-semibold tracking-tight">Jadłospis na ten tydzień</h2>
          <p className="mt-3 text-slate-600">
            Wybierz dietę i sprawdź, co gotujemy w kolejne dni. Menu zmienia się co tydzień, żeby
            nigdy się nie znudziło.
          </p>
        </div>
        {dietPlans && dietPlans.length > 0 && (
          <div role="group" aria-label="Wybór diety" className="mt-8 flex flex-wrap gap-2">
            {dietPlans.map((dietPlan) => {
              const selected = dietPlan.id === selectedDiet?.id
              return (
                <button
                  key={dietPlan.id}
                  type="button"
                  aria-pressed={selected}
                  onClick={() => selectDiet(dietPlan.id)}
                  className={`rounded-full px-4 py-2 text-sm font-medium transition ${
                    selected
                      ? 'bg-brand text-brand-foreground shadow-sm'
                      : 'bg-white text-slate-700 ring-1 ring-slate-300 hover:ring-brand'
                  }`}
                >
                  {dietPlan.name}
                  <span className={selected ? 'ml-1.5 opacity-80' : 'ml-1.5 text-slate-400'}>
                    {dietPlan.kcal} kcal
                  </span>
                </button>
              )
            })}
          </div>
        )}
        <div className="mt-6">
          <WeeklyMenu diet={selectedDiet} />
        </div>
      </div>
    </section>
  )
}
