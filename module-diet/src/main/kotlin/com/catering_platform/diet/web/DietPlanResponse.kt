package com.catering_platform.diet.web

import com.catering_platform.diet.domain.DietPlan
import com.catering_platform.diet.domain.DietPlanState

data class DietPlanResponse(val id: String, val name: String, val kcal: Int, val state: DietPlanState) {
    companion object {
        fun fromDomain(dietPlan: DietPlan) =
            DietPlanResponse(
                id = dietPlan.id.value.toString(),
                name = dietPlan.name,
                kcal = dietPlan.kcal,
                state = dietPlan.state
            )
    }
}