import { Leaf } from 'lucide-react'
import { useId, useRef, useState, type KeyboardEvent } from 'react'
import type { MealType, MenuDay, MenuDiet } from '@/features/menu/menuApi'
import { useWeeklyMenu } from '@/features/menu/useWeeklyMenu'

const mealLabels: Record<MealType, string> = {
  BREAKFAST: 'Śniadanie',
  SECOND_BREAKFAST: 'II śniadanie',
  LUNCH: 'Obiad',
  AFTERNOON_SNACK: 'Podwieczorek',
  DINNER: 'Kolacja',
}

const weekdayFormat = new Intl.DateTimeFormat('pl-PL', { weekday: 'short' })
const dateFormat = new Intl.DateTimeFormat('pl-PL', { day: '2-digit', month: '2-digit' })
const fullDateFormat = new Intl.DateTimeFormat('pl-PL', {
  weekday: 'long',
  day: 'numeric',
  month: 'long',
})

function parseDate(isoDate: string): Date {
  const [year, month, day] = isoDate.split('-').map(Number)
  return new Date(year ?? 1970, (month ?? 1) - 1, day ?? 1)
}

function todayIndex(days: MenuDay[], today: Date): number {
  const index = days.findIndex((day) => parseDate(day.date).toDateString() === today.toDateString())
  return index === -1 ? 0 : index
}

interface WeeklyMenuProps {
  diet?: MenuDiet
  today?: Date
}

export function WeeklyMenu({ diet, today = new Date() }: WeeklyMenuProps) {
  const { data: menu, isPending, isError } = useWeeklyMenu(diet)

  if (isPending) {
    return <div aria-busy="true" className="h-96 animate-pulse rounded-2xl bg-slate-100" />
  }

  if (isError) {
    return (
      <p role="alert" className="text-slate-600">
        Nie udało się pobrać jadłospisu.
      </p>
    )
  }

  return (
    <MenuTabs
      key={menu.days[0]?.date}
      days={menu.days}
      initialIndex={todayIndex(menu.days, today)}
    />
  )
}

interface MenuTabsProps {
  days: MenuDay[]
  initialIndex: number
}

function MenuTabs({ days, initialIndex }: MenuTabsProps) {
  const [selectedIndex, setSelectedIndex] = useState(initialIndex)
  const tabRefs = useRef<(HTMLButtonElement | null)[]>([])
  const idPrefix = useId()
  const selectedDay = days[selectedIndex]

  function selectTab(index: number) {
    const nextIndex = (index + days.length) % days.length
    setSelectedIndex(nextIndex)
    tabRefs.current[nextIndex]?.focus()
  }

  function handleKeyDown(event: KeyboardEvent<HTMLButtonElement>) {
    const keyActions: Record<string, () => void> = {
      ArrowRight: () => selectTab(selectedIndex + 1),
      ArrowLeft: () => selectTab(selectedIndex - 1),
      Home: () => selectTab(0),
      End: () => selectTab(days.length - 1),
    }
    const action = keyActions[event.key]
    if (action) {
      event.preventDefault()
      action()
    }
  }

  if (!selectedDay) {
    return null
  }

  const totalKcal = selectedDay.meals.reduce((sum, meal) => sum + meal.kcal, 0)

  return (
    <div className="overflow-hidden rounded-2xl bg-white ring-1 ring-slate-200">
      <div
        role="tablist"
        aria-label="Dni tygodnia"
        className="flex gap-1 overflow-x-auto border-b border-slate-200 bg-slate-50 p-2"
      >
        {days.map((day, index) => {
          const date = parseDate(day.date)
          const selected = index === selectedIndex
          return (
            <button
              key={day.date}
              ref={(element) => {
                tabRefs.current[index] = element
              }}
              id={`${idPrefix}-tab-${index}`}
              type="button"
              role="tab"
              aria-selected={selected}
              aria-controls={`${idPrefix}-panel`}
              tabIndex={selected ? 0 : -1}
              onClick={() => setSelectedIndex(index)}
              onKeyDown={handleKeyDown}
              className={`flex min-w-16 flex-1 flex-col items-center rounded-xl px-3 py-2 text-sm transition ${
                selected
                  ? 'bg-white font-semibold text-brand-strong shadow-sm ring-1 ring-slate-200'
                  : 'text-slate-500 hover:bg-white/70 hover:text-slate-700'
              }`}
            >
              <span className="capitalize">{weekdayFormat.format(date)}</span>
              <span className="text-xs font-normal">{dateFormat.format(date)}</span>
            </button>
          )
        })}
      </div>
      <div
        id={`${idPrefix}-panel`}
        role="tabpanel"
        aria-labelledby={`${idPrefix}-tab-${selectedIndex}`}
        className="p-6"
      >
        <div className="flex flex-wrap items-baseline justify-between gap-2">
          <h3 className="text-lg font-semibold first-letter:uppercase">
            {fullDateFormat.format(parseDate(selectedDay.date))}
          </h3>
          <p className="text-sm text-slate-500">
            Razem <span className="font-semibold text-slate-900">{totalKcal}</span> kcal
          </p>
        </div>
        <ul className="mt-4 divide-y divide-slate-100">
          {selectedDay.meals.map((meal) => (
            <li key={meal.type} className="flex items-start justify-between gap-4 py-4">
              <div>
                <p className="text-sm font-medium text-brand-strong">{mealLabels[meal.type]}</p>
                <p className="mt-0.5 text-slate-800">{meal.name}</p>
                {meal.vegetarian && (
                  <span className="mt-2 inline-flex items-center gap-1 rounded-full bg-brand-soft px-2 py-0.5 text-xs font-medium text-brand-strong">
                    <Leaf aria-hidden className="size-3" />
                    Wegetariańskie
                  </span>
                )}
              </div>
              <span className="shrink-0 text-sm text-slate-500">{meal.kcal} kcal</span>
            </li>
          ))}
        </ul>
      </div>
    </div>
  )
}
