import { toApiError } from '@/api/apiError'
import { apiClient, type ApiSchemas } from '@/api/client'

export type DietPlan = ApiSchemas['DietPlanResponse']
export type DietPlanState = DietPlan['state']
export type CreateDietPlanRequest = ApiSchemas['CreateDietPlanRequest']

export const DIET_PLAN_NAME_MAX_LENGTH = 200

export async function fetchDietPlans(): Promise<DietPlan[]> {
  const { data, error, response } = await apiClient.GET('/api/v1/shop/diet-plans')
  if (!data) {
    throw toApiError(response, error)
  }
  return data
}

export async function createDietPlan(request: CreateDietPlanRequest): Promise<DietPlan> {
  const { data, error, response } = await apiClient.POST('/api/v1/admin/diet-plans', {
    body: request,
  })
  if (!data) {
    throw toApiError(response, error)
  }
  return data
}
