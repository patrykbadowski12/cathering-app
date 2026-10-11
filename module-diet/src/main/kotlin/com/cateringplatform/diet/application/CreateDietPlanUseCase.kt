package com.cateringplatform.diet.application

import com.cateringplatform.diet.domain.DietPlan
import com.cateringplatform.diet.domain.DietPlanRepository
import com.cateringplatform.shared.tenant.TenantId
import org.springframework.transaction.annotation.Transactional

@Transactional
class CreateDietPlanUseCase(private val repository: DietPlanRepository) {
    fun execute(tenantId: TenantId, name: String, kcal: Int): DietPlan {
        val dietPlan = DietPlan.create(tenantId, name, kcal)
        return repository.save(dietPlan)
    }
}
