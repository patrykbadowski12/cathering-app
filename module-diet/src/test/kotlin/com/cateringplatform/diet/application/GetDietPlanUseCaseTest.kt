package com.cateringplatform.diet.application

import com.cateringplatform.diet.domain.DietPlan
import com.cateringplatform.diet.domain.DietPlanId
import com.cateringplatform.shared.TenantId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.UUID

class GetDietPlanUseCaseTest {

    private val inMemoryDietPlanRepository = InMemoryDietPlanRepository()
    private val getDietPlanUseCase = GetDietPlanUseCase(inMemoryDietPlanRepository)

    @Test
    fun `should find diet plan by id and tenantId`() {
        // given
        val tenantId = TenantId(UUID.randomUUID())
        val dietPlan = DietPlan.create(tenantId, "Test name", 10)
        inMemoryDietPlanRepository.save(dietPlan)

        // when
        val result = getDietPlanUseCase.execute(dietPlan.id, tenantId)

        // then
        assertEquals(dietPlan, result)
    }

    @Test
    fun `should not find diet for different tenantId`() {
        // given
        val tenantId = TenantId(UUID.randomUUID())
        val dietPlan = DietPlan.create(tenantId, "Test name", 10)
        inMemoryDietPlanRepository.save(dietPlan)

        // when
        val result = getDietPlanUseCase.execute(dietPlan.id, TenantId(UUID.randomUUID()))

        // then
        assertNull(result)
    }

    @Test
    fun `should not find diet plan by id`() {
        // when
        val result = getDietPlanUseCase.execute(DietPlanId(UUID.randomUUID()), TenantId(UUID.randomUUID()))

        // then
        assertNull(result)
    }
}
