import { screen } from '@testing-library/react'
import { http, HttpResponse } from 'msw'
import { describe, expect, it } from 'vitest'
import { DietPlanList } from '@/features/diet-plans/DietPlanList'
import type { DietPlan } from '@/features/diet-plans/dietPlansApi'
import { renderWithProviders } from '@/test/render'
import { server } from '@/test/server'

function serveDietPlans(dietPlans: DietPlan[]) {
  server.use(http.get('/api/v1/shop/diet-plans', () => HttpResponse.json(dietPlans)))
}

describe('DietPlanList', () => {
  it('shows active diet plans', async () => {
    serveDietPlans([
      { id: '1', name: 'Keto', kcal: 1500, state: 'ACTIVE' },
      { id: '2', name: 'Wege', kcal: 1800, state: 'ACTIVE' },
      { id: '3', name: 'Archiwalna', kcal: 2000, state: 'INACTIVE' },
    ])

    renderWithProviders(<DietPlanList />)

    const keto = await screen.findByRole('heading', { name: 'Keto' })
    expect(keto.closest('article')).toHaveTextContent('1500 kcal dziennie')
    expect(screen.getAllByRole('link', { name: 'Zobacz jadłospis' })[0]).toHaveAttribute(
      'href',
      '/?dieta=1#jadlospis',
    )
    expect(screen.getByRole('heading', { name: 'Wege' })).toBeInTheDocument()
    expect(screen.queryByRole('heading', { name: 'Archiwalna' })).not.toBeInTheDocument()
  })

  it('shows empty state when there are no active diet plans', async () => {
    serveDietPlans([])

    renderWithProviders(<DietPlanList />)

    expect(await screen.findByText('Wkrótce pojawią się tutaj nasze diety.')).toBeInTheDocument()
  })

  it('shows error when diet plans cannot be loaded', async () => {
    server.use(http.get('/api/v1/shop/diet-plans', () => new HttpResponse(null, { status: 500 })))

    renderWithProviders(<DietPlanList />)

    expect(await screen.findByRole('alert')).toHaveTextContent('Nie udało się pobrać listy diet.')
  })
})
