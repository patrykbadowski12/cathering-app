import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import { DietPlanList } from '@/features/diet-plans/DietPlanList'
import type { DietPlan } from '@/features/diet-plans/dietPlansApi'

function stubFetch(response: Response) {
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue(response))
}

function jsonResponse(body: DietPlan[]) {
  return new Response(JSON.stringify(body), {
    status: 200,
    headers: { 'Content-Type': 'application/json' },
  })
}

function renderList() {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
  return render(
    <QueryClientProvider client={queryClient}>
      <DietPlanList />
    </QueryClientProvider>,
  )
}

describe('DietPlanList', () => {
  it('shows active diet plans', async () => {
    stubFetch(
      jsonResponse([
        { id: '1', name: 'Keto', kcal: 1500, state: 'ACTIVE' },
        { id: '2', name: 'Wege', kcal: 1800, state: 'ACTIVE' },
        { id: '3', name: 'Archiwalna', kcal: 2000, state: 'INACTIVE' },
      ]),
    )

    renderList()

    const keto = await screen.findByRole('heading', { name: 'Keto' })
    expect(keto.closest('article')).toHaveTextContent('1500 kcal dziennie')
    expect(screen.getByRole('heading', { name: 'Wege' })).toBeInTheDocument()
    expect(screen.queryByRole('heading', { name: 'Archiwalna' })).not.toBeInTheDocument()
  })

  it('shows empty state when there are no active diet plans', async () => {
    stubFetch(jsonResponse([]))

    renderList()

    expect(await screen.findByText('Wkrótce pojawią się tutaj nasze diety.')).toBeInTheDocument()
  })

  it('shows error when diet plans cannot be loaded', async () => {
    stubFetch(new Response(null, { status: 500 }))

    renderList()

    expect(await screen.findByRole('alert')).toHaveTextContent('Nie udało się pobrać listy diet.')
  })
})
