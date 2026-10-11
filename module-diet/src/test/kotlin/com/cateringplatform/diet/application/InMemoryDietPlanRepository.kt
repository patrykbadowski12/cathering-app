package com.cateringplatform.diet.application

import com.cateringplatform.diet.domain.DietPlan
import com.cateringplatform.diet.domain.DietPlanId
import com.cateringplatform.diet.domain.DietPlanRepository
import com.cateringplatform.shared.tenant.TenantId

class InMemoryDietPlanRepository : DietPlanRepository {

    private val dietCollection = HashMap<DietPlanId, DietPlan>()

    override fun save(dietPlan: DietPlan): DietPlan {
        dietCollection[dietPlan.id] = dietPlan
        return dietPlan
    }

    override fun findByIdAndTenantId(id: DietPlanId, tenantId: TenantId): DietPlan? =
        dietCollection[id]?.takeIf { it.tenantId == tenantId }

    override fun findAllByTenantId(tenantId: TenantId): List<DietPlan> = dietCollection
        .filter { (key, value) -> value.tenantId == tenantId }
        .values
        .toList()
}
