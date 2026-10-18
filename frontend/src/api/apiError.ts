import type { ApiSchemas } from '@/api/client'

export type ProblemDetail = ApiSchemas['ProblemDetail']
export type FieldViolation = ApiSchemas['FieldViolation']

export class ApiError extends Error {
  readonly status: number
  readonly problem: ProblemDetail | undefined

  constructor(status: number, problem?: ProblemDetail) {
    super(problem?.detail ?? problem?.title ?? `Request failed with status ${status}`)
    this.name = 'ApiError'
    this.status = status
    this.problem = problem
  }

  get fieldErrors(): FieldViolation[] {
    return this.problem?.errors ?? []
  }
}

function isProblemDetail(body: unknown): body is ProblemDetail {
  return typeof body === 'object' && body !== null && !Array.isArray(body)
}

export function toApiError(response: Response, body: unknown): ApiError {
  return new ApiError(response.status, isProblemDetail(body) ? body : undefined)
}
