package com.cateringplatform.diet.domain

class DietVariantNotFoundException(id: DietVariantId) : RuntimeException("Could not find variant with id ${id.value}!")
