package com.catering_platform.diet.application

import com.catering_platform.diet.domain.DietPlanState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class CreateDietPlanUseCaseTest {

    private val inMemoryDietPlanRepository = InMemoryDietPlanRepository()
    private val createDietPlanUseCase = CreateDietPlanUseCase(inMemoryDietPlanRepository)

    @Test
    fun `should properly save diet plan`() {
        // given
        val tenantId = UUID.randomUUID()
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
        val tenantId = UUID.randomUUID()
        val name = "   "
        val kcal = 10

        // when
        assertThrows<IllegalArgumentException> {
            createDietPlanUseCase.execute(tenantId, name, kcal)
        }

        // then
        val result = inMemoryDietPlanRepository.findAllByTenantId(tenantId)
        assertTrue(result.isEmpty())
    }

}