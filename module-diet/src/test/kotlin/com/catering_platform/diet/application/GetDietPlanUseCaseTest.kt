package com.catering_platform.diet.application

import com.catering_platform.diet.domain.DietPlan
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.UUID

class GetDietPlanUseCaseTest {

    private val inMemoryDietPlanRepository = InMemoryDietPlanRepository()
    private val getDietPlanUseCase = GetDietPlanUseCase(inMemoryDietPlanRepository)

    @Test
    fun `should find diet plan by id and tenantId`() {
        // given
        val tenantId = UUID.randomUUID()
        val dietPlan = DietPlan.create(tenantId, "Test name", 10)
        inMemoryDietPlanRepository.save(dietPlan)

        // when
        val result = getDietPlanUseCase.execute(dietPlan.id.value, tenantId)

        // then
        assertEquals(dietPlan, result)
    }

    @Test
    fun `should not find diet for different tenantId`() {
        // given
        val tenantId = UUID.randomUUID()
        val dietPlan = DietPlan.create(tenantId, "Test name", 10)
        inMemoryDietPlanRepository.save(dietPlan)

        // when
        val result = getDietPlanUseCase.execute(dietPlan.id.value, UUID.randomUUID())

        // then
        assertNull(result)
    }

    @Test
    fun `should not find diet plan by id`() {

        // when
        val result = getDietPlanUseCase.execute(UUID.randomUUID(), UUID.randomUUID())

        // then
        assertNull(result)
    }

}