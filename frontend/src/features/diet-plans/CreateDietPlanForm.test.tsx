import { screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { http, HttpResponse } from 'msw'
import { describe, expect, it } from 'vitest'
import { CreateDietPlanForm } from '@/features/diet-plans/CreateDietPlanForm'
import type { CreateDietPlanRequest, DietPlan } from '@/features/diet-plans/dietPlansApi'
import { renderWithProviders } from '@/test/render'
import { server } from '@/test/server'

async function fillForm(name: string, kcal: string) {
  const user = userEvent.setup()
  if (name) {
    await user.type(screen.getByLabelText('Nazwa'), name)
  }
  if (kcal) {
    await user.type(screen.getByLabelText('Kaloryczność (kcal dziennie)'), kcal)
  }
  await user.click(screen.getByRole('button', { name: 'Dodaj dietę' }))
}

describe('CreateDietPlanForm', () => {
  it('creates a diet plan and clears the form', async () => {
    const requests: CreateDietPlanRequest[] = []
    server.use(
      http.post('/api/v1/admin/diet-plans', async ({ request }) => {
        const body = (await request.json()) as CreateDietPlanRequest
        requests.push(body)
        const created: DietPlan = { id: 'new-id', ...body, state: 'ACTIVE' }
        return HttpResponse.json(created, { status: 201 })
      }),
    )
    renderWithProviders(<CreateDietPlanForm />)

    await fillForm('  Keto  ', '1500')

    expect(await screen.findByRole('status')).toHaveTextContent('Dodano dietę „Keto”.')
    expect(requests).toEqual([{ name: 'Keto', kcal: 1500 }])
    expect(screen.getByLabelText('Nazwa')).toHaveValue('')
  })

  it('shows validation errors without calling the API', async () => {
    renderWithProviders(<CreateDietPlanForm />)

    await fillForm('', '0')

    expect(screen.getByLabelText('Nazwa')).toHaveAccessibleDescription('Podaj nazwę diety')
    expect(screen.getByLabelText('Kaloryczność (kcal dziennie)')).toHaveAccessibleDescription(
      'Kaloryczność musi być większa od zera',
    )
  })

  it('requires kcal', async () => {
    renderWithProviders(<CreateDietPlanForm />)

    await fillForm('Keto', '')

    expect(screen.getByLabelText('Kaloryczność (kcal dziennie)')).toHaveAccessibleDescription(
      'Podaj kaloryczność',
    )
  })

  it('shows field errors from ProblemDetail next to the fields', async () => {
    server.use(
      http.post('/api/v1/admin/diet-plans', () =>
        HttpResponse.json(
          {
            type: 'about:blank',
            title: 'Bad Request',
            status: 400,
            errors: [{ field: 'name', message: 'Dieta o tej nazwie już istnieje' }],
          },
          { status: 400, headers: { 'Content-Type': 'application/problem+json' } },
        ),
      ),
    )
    renderWithProviders(<CreateDietPlanForm />)

    await fillForm('Keto', '1500')

    expect(await screen.findByText('Dieta o tej nazwie już istnieje')).toBeInTheDocument()
    expect(screen.getByLabelText('Nazwa')).toHaveAttribute('aria-invalid', 'true')
    expect(screen.getByLabelText('Nazwa')).toHaveValue('Keto')
  })

  it('shows a general error when the server fails', async () => {
    server.use(http.post('/api/v1/admin/diet-plans', () => new HttpResponse(null, { status: 500 })))
    renderWithProviders(<CreateDietPlanForm />)

    await fillForm('Keto', '1500')

    expect(await screen.findByRole('alert')).toHaveTextContent(
      'Nie udało się zapisać diety. Spróbuj ponownie.',
    )
  })
})
