import { useQuery } from '@tanstack/react-query'
import { fetchDietPlans } from '@/features/diet-plans/dietPlansApi'

export const dietPlansQueryKey = ['diet-plans']

export function useDietPlans() {
  return useQuery({
    queryKey: dietPlansQueryKey,
    queryFn: fetchDietPlans,
    select: (dietPlans) => dietPlans.filter((dietPlan) => dietPlan.state === 'ACTIVE'),
  })
}

// TODO(E1): switch to the admin list endpoint (shop hides internal fields and inactive diets).
export function useAllDietPlans() {
  return useQuery({
    queryKey: dietPlansQueryKey,
    queryFn: fetchDietPlans,
  })
}
