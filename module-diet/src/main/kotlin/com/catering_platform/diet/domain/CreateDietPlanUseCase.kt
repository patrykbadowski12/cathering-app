package com.catering_platform.diet.domain

class CreateDietPlanUseCase(
    private val repository: DietPlanRepository,
) {
    fun execute(name: String, kcal: Int): DietPlan {
        val dietPlan = DietPlan.create(name, kcal)
        return repository.save(dietPlan)
    }
}