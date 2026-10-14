import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import type { DietPlan } from '@/features/diet-plans/dietPlansApi'
import { WeeklyMenuSection } from '@/shop/home/WeeklyMenuSection'

const dietPlans: DietPlan[] = [
  { id: 'keto-id', name: 'Keto', kcal: 1500, state: 'ACTIVE' },
  { id: 'sport-id', name: 'Sport', kcal: 2500, state: 'ACTIVE' },
]

function renderSection(initialEntry = '/') {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={[initialEntry]}>
        <WeeklyMenuSection />
      </MemoryRouter>
    </QueryClientProvider>,
  )
}

async function dayTotalKcal(): Promise<number> {
  const panel = await screen.findByRole('tabpanel')
  const total = within(panel).getByText(/^Razem/)
  return Number(total.textContent?.replace(/\D/g, ''))
}

describe('WeeklyMenuSection', () => {
  beforeEach(() => {
    window.HTMLElement.prototype.scrollIntoView = vi.fn()
    vi.stubGlobal(
      'fetch',
      vi.fn().mockImplementation(() =>
        Promise.resolve(
          new Response(JSON.stringify(dietPlans), {
            status: 200,
            headers: { 'Content-Type': 'application/json' },
          }),
        ),
      ),
    )
  })

  it('selects the first diet by default', async () => {
    renderSection()

    expect(await screen.findByRole('button', { name: /Keto/ })).toHaveAttribute(
      'aria-pressed',
      'true',
    )
    expect(Math.abs((await dayTotalKcal()) - 1500)).toBeLessThanOrEqual(30)
  })

  it('switches the menu to the chosen diet', async () => {
    const user = userEvent.setup()
    renderSection()

    await user.click(await screen.findByRole('button', { name: /Sport/ }))

    expect(screen.getByRole('button', { name: /Sport/ })).toHaveAttribute('aria-pressed', 'true')
    expect(Math.abs((await dayTotalKcal()) - 2500)).toBeLessThanOrEqual(30)
  })

  it('opens with the diet from the link', async () => {
    renderSection('/?dieta=sport-id#jadlospis')

    expect(await screen.findByRole('button', { name: /Sport/ })).toHaveAttribute(
      'aria-pressed',
      'true',
    )
  })
})
