package com.catering_platform.tenant

import jakarta.servlet.http.HttpServletRequest

interface TenantResolver {
    fun resolveTenant(request: HttpServletRequest): ResolvedTenant
}