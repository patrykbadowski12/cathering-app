package com.catering_platform.diet.application

import com.catering_platform.diet.domain.DietPlan
import com.catering_platform.diet.domain.DietPlanRepository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
class CreateDietPlanUseCase(
    private val repository: DietPlanRepository,
) {
    fun execute(tenantId: UUID, name: String, kcal: Int): DietPlan {
        val dietPlan = DietPlan.create(tenantId, name, kcal)
        return repository.save(dietPlan)
    }
}