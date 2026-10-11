package com.cateringplatform.diet.application

import com.cateringplatform.diet.domain.DietPlan
import com.cateringplatform.diet.domain.DietPlanRepository
import com.cateringplatform.shared.tenant.TenantId
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
class ListDietPlanUseCase(private val repository: DietPlanRepository) {
    fun execute(tenantId: TenantId): List<DietPlan> = repository.findAllByTenantId(tenantId)
}
