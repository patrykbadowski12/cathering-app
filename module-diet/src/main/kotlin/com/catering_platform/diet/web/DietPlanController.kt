package com.catering_platform.diet.web

import com.catering_platform.diet.domain.CreateDietPlanUseCase
import com.catering_platform.diet.domain.GetDietPlansUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


data class CreateDietPlanRequest(
    val name: String,
    val kcal: Int
)

data class DietPlanResponse(val id: String, val name: String)

@RestController
@RequestMapping("/diet-plans")
class DietPlanController(
    private val createDietPlanUseCase: CreateDietPlanUseCase,
    private val getDietPlanUseCase: GetDietPlansUseCase
) {
    @PostMapping
    fun create(@RequestBody request: CreateDietPlanRequest): DietPlanResponse {
        val plan = createDietPlanUseCase.execute(request.name, request.kcal)
        return DietPlanResponse(plan.id.value.toString(), plan.name)
    }
}