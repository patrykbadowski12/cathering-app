package com.cateringplatform.diet.infrastructure

import com.cateringplatform.diet.domain.DietPlan
import com.cateringplatform.diet.domain.DietPlanId
import com.cateringplatform.diet.domain.DietPlanRepository
import com.cateringplatform.shared.tenant.TenantId
import org.springframework.stereotype.Repository

@Repository
class DietPlanRepositoryImpl(private val jpaRepository: DietPlanJpaRepository) : DietPlanRepository {

    override fun save(dietPlan: DietPlan): DietPlan = DietPlanEntity.fromDomain(dietPlan)
        .let { jpaRepository.save(it) }.toDietPlan()

    override fun findByIdAndTenantId(id: DietPlanId, tenantId: TenantId): DietPlan? =
        jpaRepository.findByIdAndTenantId(id.value, tenantId.value)?.toDietPlan()

    override fun findAllByTenantId(tenantId: TenantId): List<DietPlan> = jpaRepository.findAllByTenantId(tenantId.value)
        .map { it.toDietPlan() }
}
