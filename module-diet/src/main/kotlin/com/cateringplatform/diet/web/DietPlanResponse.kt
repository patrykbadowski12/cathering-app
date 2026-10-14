package com.cateringplatform.diet.web

import com.cateringplatform.diet.domain.DietPlan
import com.cateringplatform.diet.domain.DietPlanState
import java.util.UUID

data class DietPlanResponse(
    val id: UUID,
    val name: String,
    val kcal: Int,
    val state: DietPlanState,
) {
    companion object {
        fun fromDomain(dietPlan: DietPlan) = DietPlanResponse(
            id = dietPlan.id.value,
            name = dietPlan.name,
            kcal = dietPlan.kcal,
            state = dietPlan.state,
        )
    }
}
