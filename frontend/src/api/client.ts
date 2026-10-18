import createClient from 'openapi-fetch'
import type { components, paths } from '@/api/schema'

export type ApiSchemas = components['schemas']

export const apiClient = createClient<paths>({
  baseUrl: window.location.origin,
  fetch: (request) => globalThis.fetch(request),
})
