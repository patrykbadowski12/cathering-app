package com.catering_platform.diet.domain

import java.util.UUID

class GetDietPlansUseCase(
    private val repository: DietPlanRepository
) {
    fun execute(tenant: UUID): List<DietPlan> {
        return repository.findAllByTenantId(tenant)
    }
}
