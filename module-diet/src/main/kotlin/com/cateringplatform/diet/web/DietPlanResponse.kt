package com.cateringplatform.diet.web

import com.cateringplatform.diet.domain.DietPlan
import com.cateringplatform.diet.domain.DietPlanState

data class DietPlanResponse(
    val id: String,
    val name: String,
    val kcal: Int,
    val state: DietPlanState,
) {
    companion object {
        fun fromDomain(dietPlan: DietPlan) = DietPlanResponse(
            id = dietPlan.id.value.toString(),
            name = dietPlan.name,
            kcal = dietPlan.kcal,
            state = dietPlan.state,
        )
    }
}
