package com.cateringplatform.shared.money

import com.cateringplatform.shared.error.DomainValidationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MoneyTest {

    @Test
    fun `should keep amount in grosze`() {
        assertEquals(5990L, Money(5990).amount)
    }

    @Test
    fun `should allow zero`() {
        assertEquals(0L, Money(0).amount)
    }

    @Test
    fun `should reject negative amount`() {
        assertThrows<DomainValidationException> { Money(-1) }
    }

    @Test
    fun `should be equal by amount`() {
        assertEquals(Money(5990), Money(5990))
    }

    @Test
    fun `should compare by amount`() {
        assertTrue(Money(5990) < Money(6990))
        assertEquals(Money(5990), listOf(Money(6990), Money(5990)).min())
    }
}
