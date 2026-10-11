package com.cateringplatform.diet.domain

import com.cateringplatform.shared.tenant.TenantId

interface DietPlanRepository {
    fun save(dietPlan: DietPlan): DietPlan
    fun findByIdAndTenantId(id: DietPlanId, tenantId: TenantId): DietPlan?
    fun findAllByTenantId(tenantId: TenantId): List<DietPlan>
}
