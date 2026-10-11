package com.cateringplatform.tenant

import com.cateringplatform.shared.TenantContext
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10) // filter must be invoked before spring security -100
class TenantFilter(private val tenantResolver: TenantResolver) : OncePerRequestFilter() {

    companion object {
        const val TENANT_KEY = "tenant"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val resolvedTenant = tenantResolver.resolveTenant(request)
        TenantContext.set(resolvedTenant.id)
        MDC.put(TENANT_KEY, resolvedTenant.slug)
        try {
            filterChain.doFilter(request, response)
        } finally {
            TenantContext.clear()
            MDC.remove(TENANT_KEY)
        }
    }
}
