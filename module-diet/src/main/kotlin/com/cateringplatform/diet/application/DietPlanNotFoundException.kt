package com.cateringplatform.diet.application

import com.cateringplatform.diet.domain.DietPlanId

class DietPlanNotFoundException(dietPlanId: DietPlanId) : RuntimeException("Diet plan ${dietPlanId.value} not found")
