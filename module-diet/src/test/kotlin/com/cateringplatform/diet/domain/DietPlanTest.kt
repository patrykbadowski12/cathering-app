package com.cateringplatform.diet.domain

import com.cateringplatform.shared.error.FieldViolation
import com.cateringplatform.shared.tenant.TenantId
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
        val tenantId = TenantId(UUID.randomUUID())

        // when
        val result = DietPlan.create(tenantId, TEST_NAME, TEST_KCAL)

        // then
        assertEquals(TEST_NAME, result.name)
        assertEquals(TEST_KCAL, result.kcal)
        assertEquals(tenantId, result.tenantId)
        assertEquals(DietState.ACTIVE, result.state)
    }

    @Test
    fun `should create a new DietPlan with 1 kcal`() {
        // given
        val tenantId = TenantId(UUID.randomUUID())

        // when
        val result = DietPlan.create(tenantId, TEST_NAME, 1)

        // then
        assertEquals(TEST_NAME, result.name)
        assertEquals(1, result.kcal)
        assertEquals(tenantId, result.tenantId)
        assertEquals(DietState.ACTIVE, result.state)
    }

    @Test
    fun `should throw exception when name is empty`() {
        // given & when
        val exception = assertThrows<InvalidDietException> {
            DietPlan.create(TenantId(UUID.randomUUID()), "", TEST_KCAL)
        }

        // then
        assertEquals(listOf(FieldViolation("name", "Diet plan name must not be blank")), exception.violations)
    }

    @Test
    fun `should throw exception when name is fake empty`() {
        // given & when
        val exception = assertThrows<InvalidDietException> {
            DietPlan.create(TenantId(UUID.randomUUID()), "      ", TEST_KCAL)
        }

        // then
        assertEquals(listOf(FieldViolation("name", "Diet plan name must not be blank")), exception.violations)
    }

    @Test
    fun `should create a new DietPlan with name of max length`() {
        // given
        val name = "a".repeat(DietPlan.MAX_NAME_LENGTH)

        // when
        val result = DietPlan.create(TenantId(UUID.randomUUID()), name, TEST_KCAL)

        // then
        assertEquals(name, result.name)
    }

    @Test
    fun `should throw exception when name is longer than max length`() {
        // given & when
        val exception = assertThrows<InvalidDietException> {
            DietPlan.create(TenantId(UUID.randomUUID()), "a".repeat(DietPlan.MAX_NAME_LENGTH + 1), TEST_KCAL)
        }

        // then
        assertEquals(
            listOf(FieldViolation("name", "Diet plan name must be at most 200 characters")),
            exception.violations,
        )
    }

    @Test
    fun `should throw exception when kcal is negative value`() {
        // given & when
        val exception = assertThrows<InvalidDietException> {
            DietPlan.create(TenantId(UUID.randomUUID()), TEST_NAME, -1)
        }

        // then
        assertEquals(listOf(FieldViolation("kcal", "Diet plan kcal must be greater than zero")), exception.violations)
    }

    @Test
    fun `should throw exception when kcal is zero`() {
        // given & when
        val exception = assertThrows<InvalidDietException> {
            DietPlan.create(TenantId(UUID.randomUUID()), TEST_NAME, 0)
        }

        // then
        assertEquals(listOf(FieldViolation("kcal", "Diet plan kcal must be greater than zero")), exception.violations)
    }
}
