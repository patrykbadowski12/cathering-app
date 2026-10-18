package com.cateringplatform.diet.application

import com.cateringplatform.diet.domain.DietPlan
import com.cateringplatform.diet.domain.DietPlanId
import com.cateringplatform.diet.domain.DietPlanRepository
import com.cateringplatform.shared.tenant.TenantId
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
class GetDietPlanUseCase(private val repository: DietPlanRepository) {
    fun execute(dietPlanId: DietPlanId, tenantId: TenantId): DietPlan =
        repository.findByIdAndTenantId(dietPlanId, tenantId) ?: throw DietPlanNotFoundException(dietPlanId)
}
