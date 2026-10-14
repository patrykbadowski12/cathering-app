export type MealType = 'BREAKFAST' | 'SECOND_BREAKFAST' | 'LUNCH' | 'AFTERNOON_SNACK' | 'DINNER'

export interface Meal {
  type: MealType
  name: string
  kcal: number
  vegetarian: boolean
}

export interface MenuDay {
  date: string
  meals: Meal[]
}

export interface WeeklyMenu {
  dietName: string
  days: MenuDay[]
}

type MealTemplate = Omit<Meal, 'type'>

const sampleWeek: MealTemplate[][] = [
  [
    { name: 'Owsianka z jabłkiem i cynamonem', kcal: 380, vegetarian: true },
    { name: 'Jogurt naturalny z granolą', kcal: 220, vegetarian: true },
    { name: 'Kurczak w ziołach z kaszą bulgur i brokułami', kcal: 610, vegetarian: false },
    { name: 'Hummus z warzywami do maczania', kcal: 190, vegetarian: true },
    { name: 'Sałatka z łososiem i awokado', kcal: 440, vegetarian: false },
  ],
  [
    { name: 'Jajecznica ze szczypiorkiem i pieczywem żytnim', kcal: 410, vegetarian: true },
    { name: 'Koktajl bananowo-szpinakowy', kcal: 210, vegetarian: true },
    { name: 'Indyk w sosie curry z ryżem jaśminowym', kcal: 630, vegetarian: false },
    { name: 'Mus z mango i chia', kcal: 180, vegetarian: true },
    { name: 'Krem z dyni z pestkami', kcal: 380, vegetarian: true },
  ],
  [
    { name: 'Placuszki twarogowe z owocami leśnymi', kcal: 400, vegetarian: true },
    { name: 'Kanapka z pastą jajeczną', kcal: 240, vegetarian: true },
    { name: 'Dorsz pieczony z ziemniakami i fasolką', kcal: 590, vegetarian: false },
    { name: 'Orzechy i suszone morele', kcal: 200, vegetarian: true },
    { name: 'Tortilla z warzywami i fetą', kcal: 430, vegetarian: true },
  ],
  [
    { name: 'Pudding jaglany z malinami', kcal: 370, vegetarian: true },
    { name: 'Serek wiejski z rzodkiewką', kcal: 210, vegetarian: true },
    { name: 'Wołowina z warzywami stir-fry i makaronem ryżowym', kcal: 650, vegetarian: false },
    { name: 'Gruszka z masłem orzechowym', kcal: 190, vegetarian: true },
    { name: 'Sałatka grecka z pieczywem pełnoziarnistym', kcal: 400, vegetarian: true },
  ],
  [
    { name: 'Omlet z pomidorami i bazylią', kcal: 390, vegetarian: true },
    { name: 'Smoothie bowl z jagodami', kcal: 230, vegetarian: true },
    { name: 'Risotto z kurczakiem i szparagami', kcal: 620, vegetarian: false },
    { name: 'Batonik owsiany domowy', kcal: 210, vegetarian: true },
    { name: 'Zupa krem z pomidorów z grzankami', kcal: 360, vegetarian: true },
  ],
  [
    { name: 'Granola z jogurtem greckim i miodem', kcal: 420, vegetarian: true },
    { name: 'Wrap z indykiem i warzywami', kcal: 260, vegetarian: false },
    { name: 'Gołąbki z kaszą w sosie pomidorowym', kcal: 600, vegetarian: true },
    { name: 'Sorbet owocowy', kcal: 150, vegetarian: true },
    { name: 'Pieczony łosoś z sałatką z kaszy pęczak', kcal: 470, vegetarian: false },
  ],
  [
    { name: 'Naleśniki gryczane z twarożkiem', kcal: 410, vegetarian: true },
    { name: 'Pasta z awokado na chlebie żytnim', kcal: 240, vegetarian: true },
    { name: 'Polędwiczka wieprzowa z puree z selera', kcal: 630, vegetarian: false },
    { name: 'Sałatka owocowa', kcal: 170, vegetarian: true },
    { name: 'Leczo z cukinii z ryżem brązowym', kcal: 400, vegetarian: true },
  ],
]

const mealOrder: MealType[] = [
  'BREAKFAST',
  'SECOND_BREAKFAST',
  'LUNCH',
  'AFTERNOON_SNACK',
  'DINNER',
]

function startOfWeek(today: Date): Date {
  const monday = new Date(today.getFullYear(), today.getMonth(), today.getDate())
  const daysSinceMonday = (monday.getDay() + 6) % 7
  monday.setDate(monday.getDate() - daysSinceMonday)
  return monday
}

function toIsoDate(date: Date): string {
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${date.getFullYear()}-${month}-${day}`
}

export interface MenuDiet {
  id: string
  name: string
  kcal: number
}

const defaultDiet: MenuDiet = { id: 'standard', name: 'Standard', kcal: 2000 }

function rotationFor(dietId: string): number {
  return [...dietId].reduce((sum, char) => sum + char.charCodeAt(0), 0) % sampleWeek.length
}

function scaleToDiet(meals: MealTemplate[], dietKcal: number): MealTemplate[] {
  const dayKcal = meals.reduce((sum, meal) => sum + meal.kcal, 0)
  return meals.map((meal) => ({
    ...meal,
    kcal: Math.round((meal.kcal * dietKcal) / dayKcal / 10) * 10,
  }))
}

// TODO(E2): replace sample data with the weekly menu endpoint from the generated API client.
export async function fetchWeeklyMenu(
  diet: MenuDiet = defaultDiet,
  today: Date = new Date(),
): Promise<WeeklyMenu> {
  const monday = startOfWeek(today)
  const rotation = rotationFor(diet.id)
  return {
    dietName: diet.name,
    days: sampleWeek.map((_, dayIndex) => {
      const date = new Date(monday)
      date.setDate(monday.getDate() + dayIndex)
      const meals = sampleWeek[(dayIndex + rotation) % sampleWeek.length] ?? []
      return {
        date: toIsoDate(date),
        meals: scaleToDiet(meals, diet.kcal).map((meal, mealIndex) => ({
          ...meal,
          type: mealOrder[mealIndex] ?? 'DINNER',
        })),
      }
    }),
  }
}
