package com.catering_platform.diet.application

import com.catering_platform.diet.domain.DietPlan
import com.catering_platform.diet.domain.DietPlanRepository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional(readOnly = true)
class ListDietPlanUseCase(
    private val repository: DietPlanRepository
) {
    fun execute(tenantId: UUID): List<DietPlan> {
        return repository.findAllByTenantId(tenantId)
    }
}
