package com.cateringplatform.tenant

import com.cateringplatform.shared.tenant.NoTenantException
import com.cateringplatform.shared.tenant.TenantContext
import com.cateringplatform.shared.tenant.TenantId
import com.cateringplatform.tenant.TenantFilter.Companion.TENANT_KEY
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.api.assertThrows
import org.slf4j.MDC
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import java.util.UUID

class TenantFilterTest {

    private val tenantProperties = TenantProperties(DefaultTenantProperties(UUID.randomUUID(), "test"))

    private val tenantResolver = FixedTenantResolver(tenantProperties)

    private val tenantFilter = TenantFilter(tenantResolver)

    @AfterEach
    fun tearDown() {
        TenantContext.clear()
        MDC.clear()
    }

    @Test
    fun `should properly get data during filter`() {
        // given
        val mockHttpServletRequest = MockHttpServletRequest()
        val mockHttpServletResponse = MockHttpServletResponse()
        var tenantId: TenantId? = null
        var mdcTenantSlug: String? = null

        // when
        tenantFilter.doFilter(mockHttpServletRequest, mockHttpServletResponse, { _, _ ->
            tenantId = TenantContext.get()
            mdcTenantSlug = MDC.get(TENANT_KEY)
        })

        // then
        assertEquals(tenantProperties.default.id, tenantId?.value)
        assertEquals(tenantProperties.default.slug, mdcTenantSlug)
        assertThrows<NoTenantException> { TenantContext.get() }
        assertNull(MDC.get(TENANT_KEY))
    }

    @Test
    fun `should tenantId and MDC tenant be empty from final block`() {
        // given
        val mockHttpServletRequest = MockHttpServletRequest()
        val mockHttpServletResponse = MockHttpServletResponse()

        // when
        assertThrows<IllegalStateException> {
            tenantFilter.doFilter(mockHttpServletRequest, mockHttpServletResponse, { _, _ ->
                throw IllegalStateException("boom")
            })
        }

        // then
        assertThrows<NoTenantException> { TenantContext.get() }
        assertNull(MDC.get(TENANT_KEY))
    }
}
