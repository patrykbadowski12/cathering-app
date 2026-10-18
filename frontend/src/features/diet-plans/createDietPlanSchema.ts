import { z } from 'zod'
import { DIET_PLAN_NAME_MAX_LENGTH } from '@/features/diet-plans/dietPlansApi'

export const createDietPlanSchema = z.object({
  name: z
    .string()
    .trim()
    .min(1, 'Podaj nazwę diety')
    .max(DIET_PLAN_NAME_MAX_LENGTH, `Nazwa może mieć najwyżej ${DIET_PLAN_NAME_MAX_LENGTH} znaków`),
  kcal: z
    .number({ error: 'Podaj kaloryczność' })
    .int('Kaloryczność musi być liczbą całkowitą')
    .positive('Kaloryczność musi być większa od zera'),
})

export type CreateDietPlanValues = z.infer<typeof createDietPlanSchema>
