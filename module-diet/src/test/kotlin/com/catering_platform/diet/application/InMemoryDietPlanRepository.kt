package com.catering_platform.diet.application

import com.catering_platform.diet.domain.DietPlan
import com.catering_platform.diet.domain.DietPlanId
import com.catering_platform.diet.domain.DietPlanRepository
import com.catering_platform.shared.TenantId

class InMemoryDietPlanRepository : DietPlanRepository {

    private val dietCollection = HashMap<DietPlanId, DietPlan>()

    override fun save(dietPlan: DietPlan): DietPlan {
        dietCollection[dietPlan.id] = dietPlan
        return dietPlan
    }

    override fun findByIdAndTenantId(
        id: DietPlanId,
        tenantId: TenantId
    ): DietPlan? {
        return dietCollection[id]?.takeIf { it.tenantId == tenantId }
    }

    override fun findAllByTenantId(tenantId: TenantId): List<DietPlan> {
        return dietCollection
            .filter { (key, value) -> value.tenantId == tenantId }
            .values
            .toList()
    }
}