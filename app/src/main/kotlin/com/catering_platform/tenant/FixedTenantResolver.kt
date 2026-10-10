package com.catering_platform.tenant

import com.catering_platform.shared.TenantId
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component

@Component
class FixedTenantResolver(
    tenantProperties: TenantProperties,
) : TenantResolver {

    private val tenant = ResolvedTenant(TenantId(tenantProperties.default.id), tenantProperties.default.slug)

    override fun resolveTenant(request: HttpServletRequest): ResolvedTenant {
        return tenant
    }
}