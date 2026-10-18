package com.cateringplatform.diet.domain

import com.cateringplatform.shared.error.DomainValidationException

class InvalidDietPlanException(
    field: String,
    message: String,
) : DomainValidationException(field, message)
