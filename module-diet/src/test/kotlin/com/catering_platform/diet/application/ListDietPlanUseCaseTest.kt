package com.catering_platform.diet.application

import com.catering_platform.diet.domain.DietPlan
import com.catering_platform.shared.TenantId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

class ListDietPlanUseCaseTest {

    private val inMemoryDietPlanRepository = InMemoryDietPlanRepository()
    private val listDietPlanUseCase = ListDietPlanUseCase(inMemoryDietPlanRepository)

    @Test
    fun `should get 2 elements for requested tenant`() {
        // given
        val tenantId = TenantId(UUID.randomUUID())
        inMemoryDietPlanRepository.save(DietPlan.create(tenantId, "Test name", 10))
        inMemoryDietPlanRepository.save(DietPlan.create(tenantId, "Test name2", 12))
        inMemoryDietPlanRepository.save(DietPlan.create(TenantId(UUID.randomUUID()), "Test name3", 14))

        // when
        val result = listDietPlanUseCase.execute(tenantId)

        // then
        assertEquals(2, result.size)
        result.forEach {
            assertEquals(tenantId, it.tenantId)
        }
    }
}
