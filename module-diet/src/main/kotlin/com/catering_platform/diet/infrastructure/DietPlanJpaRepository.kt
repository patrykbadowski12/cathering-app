package com.catering_platform.diet.infrastructure

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DietPlanJpaRepository : JpaRepository<DietPlanEntity, UUID> {
    fun findByIdAndTenantId(id: UUID, tenantId: UUID): DietPlanEntity?
    fun findAllByTenantId(tenantId: UUID): List<DietPlanEntity>
}