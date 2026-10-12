import { useQuery } from '@tanstack/react-query'
import { fetchDietPlans } from '@/features/diet-plans/dietPlansApi'

export function useDietPlans() {
  return useQuery({
    queryKey: ['diet-plans'],
    queryFn: fetchDietPlans,
    select: (dietPlans) => dietPlans.filter((dietPlan) => dietPlan.state === 'ACTIVE'),
  })
}
