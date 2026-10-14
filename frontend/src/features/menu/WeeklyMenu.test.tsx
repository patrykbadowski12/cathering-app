import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, expect, it } from 'vitest'
import { WeeklyMenu } from '@/features/menu/WeeklyMenu'

function renderMenu(today: Date) {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
  return render(
    <QueryClientProvider client={queryClient}>
      <WeeklyMenu today={today} />
    </QueryClientProvider>,
  )
}

describe('WeeklyMenu', () => {
  it('shows seven days with today selected', async () => {
    renderMenu(new Date())

    const tabs = await screen.findAllByRole('tab')
    expect(tabs).toHaveLength(7)
    expect(tabs.filter((tab) => tab.getAttribute('aria-selected') === 'true')).toHaveLength(1)
  })

  it('shows meals of the selected day', async () => {
    renderMenu(new Date())

    const panel = await screen.findByRole('tabpanel')
    for (const meal of ['Śniadanie', 'II śniadanie', 'Obiad', 'Podwieczorek', 'Kolacja']) {
      expect(within(panel).getByText(meal)).toBeInTheDocument()
    }
  })

  it('switches day on click and with arrow keys', async () => {
    const user = userEvent.setup()
    renderMenu(new Date())
    const tabs = await screen.findAllByRole('tab')
    const [first, second] = tabs

    await user.click(first!)
    expect(first).toHaveAttribute('aria-selected', 'true')

    await user.keyboard('{ArrowRight}')
    expect(second).toHaveAttribute('aria-selected', 'true')
    expect(second).toHaveFocus()

    await user.keyboard('{ArrowLeft}{ArrowLeft}')
    expect(tabs[6]).toHaveAttribute('aria-selected', 'true')
  })
})
