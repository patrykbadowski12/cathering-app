package com.catering_platform.diet.domain

import java.util.UUID

interface DietPlanRepository {
    fun save(dietPlan: DietPlan): DietPlan
    fun findById(id: DietPlanId): DietPlan?
    fun findAllByTenantId(tenantId: UUID): List<DietPlan>
}