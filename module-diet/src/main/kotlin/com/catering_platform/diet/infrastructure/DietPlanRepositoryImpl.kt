package com.catering_platform.diet.infrastructure

import com.catering_platform.diet.domain.DietPlan
import com.catering_platform.diet.domain.DietPlanId
import com.catering_platform.diet.domain.DietPlanRepository
import com.catering_platform.shared.TenantId
import org.springframework.stereotype.Repository

@Repository
class DietPlanRepositoryImpl(
    private val jpaRepository: DietPlanJpaRepository,
) : DietPlanRepository {

    override fun save(dietPlan: DietPlan): DietPlan =
        DietPlanEntity.fromDomain(dietPlan)
        .let { jpaRepository.save(it) }.toDietPlan()

    override fun findByIdAndTenantId(id: DietPlanId, tenantId: TenantId): DietPlan? =
        jpaRepository.findByIdAndTenantId(id.value, tenantId.value)?.toDietPlan()

    override fun findAllByTenantId(tenantId: TenantId): List<DietPlan> =
        jpaRepository.findAllByTenantId(tenantId.value)
            .map { it.toDietPlan() }
}