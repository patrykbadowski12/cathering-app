export const MENU_SECTION_ID = 'jadlospis'
export const DIET_SEARCH_PARAM = 'dieta'

export function menuLinkFor(dietId: string) {
  return { search: `?${DIET_SEARCH_PARAM}=${dietId}`, hash: MENU_SECTION_ID }
}
