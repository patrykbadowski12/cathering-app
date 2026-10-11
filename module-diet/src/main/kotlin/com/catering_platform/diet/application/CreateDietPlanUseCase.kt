package com.catering_platform.diet.application

import com.catering_platform.diet.domain.DietPlan
import com.catering_platform.diet.domain.DietPlanRepository
import com.catering_platform.shared.TenantId
import org.springframework.transaction.annotation.Transactional

@Transactional
class CreateDietPlanUseCase(private val repository: DietPlanRepository) {
    fun execute(tenantId: TenantId, name: String, kcal: Int): DietPlan {
        val dietPlan = DietPlan.create(tenantId, name, kcal)
        return repository.save(dietPlan)
    }
}
