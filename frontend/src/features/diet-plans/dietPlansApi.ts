// TODO(T-015): replace with the client generated from OpenAPI.
export type DietPlanState = 'ACTIVE' | 'INACTIVE'

export interface DietPlan {
  id: string
  name: string
  kcal: number
  state: DietPlanState
}

export async function fetchDietPlans(): Promise<DietPlan[]> {
  const response = await fetch('/api/v1/diet-plans', { headers: { Accept: 'application/json' } })
  if (!response.ok) {
    throw new Error(`Failed to load diet plans: ${response.status}`)
  }
  return response.json() as Promise<DietPlan[]>
}
