package com.catering_platform.shared

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID
import kotlin.test.assertIs

class TenantContextTest {

    @AfterEach
    fun cleanup() {
        TenantContext.clear()
    }

    @Test
    fun `should set tenant and get it`() {
        // given
        val tenantId = TenantId(UUID.randomUUID())

        // when
        TenantContext.set(tenantId)

        // then
        assertEquals(tenantId, TenantContext.get())
    }

    @Test
    fun `should throw exception if tenant is not set`() {
        // given & when
        val exception = assertThrows<NoTenantException> {
            TenantContext.get()
        }

        // then
        assertEquals("Tenant could not be resolved", exception.message)
    }

    @Test
    fun `should throw exception after clear when tenant is not set`() {
        // given
        val tenantId = TenantId(UUID.randomUUID())
        TenantContext.set(tenantId)
        TenantContext.clear()

        // when
        val exception = assertThrows<NoTenantException> {
            TenantContext.get()
        }

        // then
        assertEquals("Tenant could not be resolved", exception.message)
    }

    @Test
    fun `should not share tenant between threads`() {
        // given
        val tenantId = TenantId(UUID.randomUUID())
        TenantContext.set(tenantId)

        // when
        var thrown: Throwable? = null
        val thread = Thread {
            thrown = runCatching { TenantContext.get() }.exceptionOrNull()
        }
        thread.start()
        thread.join()

        // then
        assertIs<NoTenantException>(thrown)
        assertEquals(tenantId, TenantContext.get())
    }
}
