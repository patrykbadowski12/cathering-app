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

    override fun save(dietPlan: DietPlan): DietPlan {
        val entity = DietPlanEntity(
            dietPlan.id.value,
            UUID.randomUUID(),
            dietPlan.name,
            kcal = dietPlan.kcal,
            state = dietPlan.state
        )
        jpaRepository.save(entity)
        return dietPlan
    }

    override fun findById(id: DietPlanId): DietPlan? =
        jpaRepository.findById(id.value).orElse(null)?.let {
            DietPlan(
                id = DietPlanId(it.id),
                name = it.name,
                tenantId = it.tenantId,
                state = it.state,
                kcal = it.kcal
            )
        }
}