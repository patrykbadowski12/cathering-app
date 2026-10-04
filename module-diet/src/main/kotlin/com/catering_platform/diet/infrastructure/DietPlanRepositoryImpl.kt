package com.catering_platform.diet.infrastructure

import com.catering_platform.diet.domain.DietPlan
import com.catering_platform.diet.domain.DietPlanId
import com.catering_platform.diet.domain.DietPlanRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class DietPlanRepositoryImpl(
    private val jpaRepository: DietPlanJpaRepository,
) : DietPlanRepository {

    override fun save(dietPlan: DietPlan): DietPlan =
        DietPlanEntity.fromDomain(dietPlan)
        .let { jpaRepository.save(it) }.toDietPlan()

    override fun findByIdAndTenantId(id: DietPlanId, tenantId: UUID): DietPlan? =
        jpaRepository.findByIdAndTenantId(id.value, tenantId)?.toDietPlan()

    override fun findAllByTenantId(tenantId: UUID): List<DietPlan> =
        jpaRepository.findAllByTenantId(tenantId)
            .map { it.toDietPlan() }
}