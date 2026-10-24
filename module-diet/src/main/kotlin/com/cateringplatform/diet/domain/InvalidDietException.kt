package com.cateringplatform.diet.domain

import com.cateringplatform.shared.error.DomainValidationException

class InvalidDietException(
    field: String,
    message: String,
) : DomainValidationException(field, message)
