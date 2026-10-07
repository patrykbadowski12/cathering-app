package com.catering_platform.diet.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class DietPlanTest {

    companion object {
        const val TEST_NAME = "Test diet name"
        const val TEST_KCAL = 1500
    }

    @Test
    fun `should create a new DietPlan`() {
        // given
        val tenantId = UUID.randomUUID()

        // when
        val result = DietPlan.create(tenantId, TEST_NAME, TEST_KCAL)

        // then
        assertEquals(TEST_NAME, result.name)
        assertEquals(TEST_KCAL, result.kcal)
        assertEquals(tenantId, result.tenantId)
        assertEquals(DietPlanState.ACTIVE, result.state)
    }

    @Test
    fun `should create a new DietPlan with 1 kcal`() {
        // given
        val tenantId = UUID.randomUUID()

        // when
        val result = DietPlan.create(tenantId, TEST_NAME, 1)

        // then
        assertEquals(TEST_NAME, result.name)
        assertEquals(1, result.kcal)
        assertEquals(tenantId, result.tenantId)
        assertEquals(DietPlanState.ACTIVE, result.state)
    }

    @Test
    fun `should throw exception when name is empty`() {
        // given & when
        val exception = assertThrows<IllegalArgumentException> {
            DietPlan.create(UUID.randomUUID(), "", TEST_KCAL)
        }

        // then
        assertEquals("Diet plan name must not be blank", exception.message)
    }

    @Test
    fun `should throw exception when name is fake empty`() {
        // given & when
        val exception = assertThrows<IllegalArgumentException> {
            DietPlan.create(UUID.randomUUID(), "      ", TEST_KCAL)
        }

        // then
        assertEquals("Diet plan name must not be blank", exception.message)
    }

    @Test
    fun `should throw exception when kcal is negative value`() {
        // given & when
        val exception = assertThrows<IllegalArgumentException> {
            DietPlan.create(UUID.randomUUID(), TEST_NAME, -1)
        }

        // then
        assertEquals("Diet plan kcal must be greater than zero" ,exception.message)
    }

    @Test
    fun `should throw exception when kcal is zero`() {
        // given & when
        val exception = assertThrows<IllegalArgumentException> {
            DietPlan.create(UUID.randomUUID(), TEST_NAME, 0)
        }

        // then
        assertEquals("Diet plan kcal must be greater than zero" ,exception.message)
    }
}