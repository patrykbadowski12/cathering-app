package com.cateringplatform.diet.application

import com.cateringplatform.diet.domain.DietPlanState
import com.cateringplatform.diet.domain.InvalidDietPlanException
import com.cateringplatform.shared.tenant.TenantId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class CreateDietPlanUseCaseTest {

    private val inMemoryDietPlanRepository = InMemoryDietPlanRepository()
    private val createDietPlanUseCase = CreateDietPlanUseCase(inMemoryDietPlanRepository)

    @Test
    fun `should properly save diet plan`() {
        // given
        val tenantId = TenantId(UUID.randomUUID())
        val name = "Test name"
        val kcal = 10

        // when
        val result = createDietPlanUseCase.execute(tenantId, name, kcal)

        // then
        assertEquals(name, result.name)
        assertEquals(kcal, result.kcal)
        assertEquals(DietPlanState.ACTIVE, result.state)
        assertEquals(tenantId, result.tenantId)
        val dietPlans = inMemoryDietPlanRepository.findAllByTenantId(tenantId)
        assertEquals(1, dietPlans.size)
    }

    @Test
    fun `should not create diet plan if name is empty`() {
        // given
        val tenantId = TenantId(UUID.randomUUID())
        val name = "   "
        val kcal = 10

        // when
        assertThrows<InvalidDietPlanException> {
            createDietPlanUseCase.execute(tenantId, name, kcal)
        }

        // then
        val result = inMemoryDietPlanRepository.findAllByTenantId(tenantId)
        assertTrue(result.isEmpty())
    }
}
