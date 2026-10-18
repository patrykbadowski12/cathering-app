import { useMutation, useQueryClient } from '@tanstack/react-query'
import { createDietPlan } from '@/features/diet-plans/dietPlansApi'
import { dietPlansQueryKey } from '@/features/diet-plans/useDietPlans'

export function useCreateDietPlan() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: createDietPlan,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: dietPlansQueryKey }),
  })
}
