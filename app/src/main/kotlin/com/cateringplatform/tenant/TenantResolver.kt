package com.cateringplatform.tenant

import jakarta.servlet.http.HttpServletRequest

interface TenantResolver {
    fun resolveTenant(request: HttpServletRequest): ResolvedTenant
}
