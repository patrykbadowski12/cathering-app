package com.catering_platform.diet.application

import com.catering_platform.diet.domain.DietPlan
import com.catering_platform.diet.domain.DietPlanId
import com.catering_platform.diet.domain.DietPlanRepository
import com.catering_platform.shared.TenantId
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
class GetDietPlanUseCase(
    private val repository: DietPlanRepository
) {
    fun execute(dietPlanId: DietPlanId, tenantId: TenantId): DietPlan? {
        return repository.findByIdAndTenantId(dietPlanId, tenantId)
    }
}
