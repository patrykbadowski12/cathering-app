// TODO(T-014): take ProblemDetail from the generated schema once the backend documents it.
export interface FieldError {
  field: string
  message: string
}

export interface ProblemDetail {
  type?: string
  title?: string
  status?: number
  detail?: string
  instance?: string
  errors?: FieldError[]
}

export class ApiError extends Error {
  readonly status: number
  readonly problem: ProblemDetail | undefined

  constructor(status: number, problem?: ProblemDetail) {
    super(problem?.detail ?? problem?.title ?? `Request failed with status ${status}`)
    this.name = 'ApiError'
    this.status = status
    this.problem = problem
  }

  get fieldErrors(): FieldError[] {
    return this.problem?.errors ?? []
  }
}

function isProblemDetail(body: unknown): body is ProblemDetail {
  return typeof body === 'object' && body !== null && !Array.isArray(body)
}

export function toApiError(response: Response, body: unknown): ApiError {
  return new ApiError(response.status, isProblemDetail(body) ? body : undefined)
}
