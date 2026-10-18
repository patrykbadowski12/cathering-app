import { screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { http, HttpResponse } from 'msw'
import { describe, expect, it } from 'vitest'
import { DietPlansAdminPage } from '@/admin/DietPlansAdminPage'
import type { CreateDietPlanRequest, DietPlan } from '@/features/diet-plans/dietPlansApi'
import { renderWithProviders } from '@/test/render'
import { server } from '@/test/server'

describe('DietPlansAdminPage', () => {
  it('shows all diet plans with their status', async () => {
    server.use(
      http.get('/api/v1/shop/diet-plans', () =>
        HttpResponse.json<DietPlan[]>([
          { id: '1', name: 'Keto', kcal: 1500, state: 'ACTIVE' },
          { id: '2', name: 'Archiwalna', kcal: 2000, state: 'INACTIVE' },
        ]),
      ),
    )

    renderWithProviders(<DietPlansAdminPage />)

    const archived = await screen.findByRole('row', { name: /Archiwalna/ })
    expect(archived).toHaveTextContent('Nieaktywna')
    expect(screen.getByRole('row', { name: /Keto/ })).toHaveTextContent('Aktywna')
  })

  it('adds a created diet plan to the list', async () => {
    const dietPlans: DietPlan[] = []
    server.use(
      http.get('/api/v1/shop/diet-plans', () => HttpResponse.json(dietPlans)),
      http.post('/api/v1/admin/diet-plans', async ({ request }) => {
        const body = (await request.json()) as CreateDietPlanRequest
        const created: DietPlan = { id: 'new-id', ...body, state: 'ACTIVE' }
        dietPlans.push(created)
        return HttpResponse.json(created, { status: 201 })
      }),
    )
    const user = userEvent.setup()
    renderWithProviders(<DietPlansAdminPage />)
    expect(await screen.findByText(/Nie masz jeszcze żadnej diety/)).toBeInTheDocument()

    await user.type(screen.getByLabelText('Nazwa'), 'Wege')
    await user.type(screen.getByLabelText('Kaloryczność (kcal dziennie)'), '1800')
    await user.click(screen.getByRole('button', { name: 'Dodaj dietę' }))

    const row = await screen.findByRole('row', { name: /Wege/ })
    expect(within(row).getByText('1800')).toBeInTheDocument()
  })
})
