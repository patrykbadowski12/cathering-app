package com.catering_platform.diet.application

import com.catering_platform.diet.domain.DietPlan
import com.catering_platform.diet.domain.DietPlanId
import com.catering_platform.diet.domain.DietPlanRepository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional(readOnly = true)
class GetDietPlanUseCase(
    private val repository: DietPlanRepository
) {
    fun execute(id: UUID, tenantId: UUID): DietPlan? {
        return repository.findByIdAndTenantId(DietPlanId(id), tenantId)
    }
}
