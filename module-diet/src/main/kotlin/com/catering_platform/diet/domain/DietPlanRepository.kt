package com.catering_platform.diet.domain

import com.catering_platform.shared.TenantId

interface DietPlanRepository {
    fun save(dietPlan: DietPlan): DietPlan
    fun findByIdAndTenantId(id: DietPlanId, tenantId: TenantId): DietPlan?
    fun findAllByTenantId(tenantId: TenantId): List<DietPlan>
}
