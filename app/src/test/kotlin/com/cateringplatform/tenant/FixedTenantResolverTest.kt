package com.cateringplatform.tenant

import com.cateringplatform.shared.tenant.TenantId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import java.util.UUID

class FixedTenantResolverTest {

    private val tenantProperties = TenantProperties(DefaultTenantProperties(UUID.randomUUID(), "brokul"))

    private val resolver = FixedTenantResolver(tenantProperties)

    @Test
    fun `should resolve default tenant from properties`() {
        // when
        val result = resolver.resolveTenant(MockHttpServletRequest())

        // then
        assertEquals(ResolvedTenant(TenantId(tenantProperties.default.id), "brokul"), result)
    }

    @Test
    fun `should resolve the same tenant regardless of request host`() {
        // given
        val firstRequest = MockHttpServletRequest().apply { serverName = "brokul.catering.pl" }
        val secondRequest = MockHttpServletRequest().apply { serverName = "inny.catering.pl" }

        // when & then
        assertEquals(resolver.resolveTenant(firstRequest), resolver.resolveTenant(secondRequest))
    }
}
