package com.catering_platform.diet.domain

import java.util.UUID

data class DietPlanId(val value: UUID)

data class DietPlan(
    val id: DietPlanId,
    val name: String,
    val tenantId: UUID,
    val state: DietPlanState,
    val kcal: Int
) {
    companion object {
        fun create(name: String, kcal: Int): DietPlan {
            require(name.isNotBlank()) { "Diet plan name must not be blank" }
            require(kcal > 0) { "Diet plan kcal must be greater than zero" }
            return DietPlan(
                id = DietPlanId(UUID.randomUUID()),
                name = name,
                kcal = kcal,
                state = DietPlanState.ACTIVE,
                tenantId = UUID.randomUUID()
            )
        }
    }
}