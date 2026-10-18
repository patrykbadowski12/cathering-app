package com.cateringplatform.tenant

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.UUID

class TenantPropertiesTest {

    private fun tenantProperties(slug: String) = TenantProperties(DefaultTenantProperties(UUID.randomUUID(), slug))

    @ParameterizedTest
    @ValueSource(strings = ["brokul", "zdrowa-micha", "catering-24", "7"])
    fun `should accept valid slug`(slug: String) {
        // when
        val result = tenantProperties(slug)

        // then
        assertEquals(slug, result.default.slug)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Brokul", "brokuł", "", "zdrowa micha", "zdrowa_micha"])
    fun `should reject invalid slug`(slug: String) {
        // when
        val exception = assertThrows<IllegalArgumentException> { tenantProperties(slug) }

        // then
        assertEquals("Invalid tenant slug '$slug': expected [a-z0-9-]+", exception.message)
    }

    @Test
    fun `should reject blank slug`() {
        assertThrows<IllegalArgumentException> { tenantProperties("   ") }
    }
}
