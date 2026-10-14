import { useQuery } from '@tanstack/react-query'
import { fetchWeeklyMenu, type MenuDiet } from '@/features/menu/menuApi'

export function useWeeklyMenu(diet: MenuDiet | undefined) {
  return useQuery({
    queryKey: ['weekly-menu', diet?.id ?? 'default', diet?.kcal],
    queryFn: () => fetchWeeklyMenu(diet),
  })
}
