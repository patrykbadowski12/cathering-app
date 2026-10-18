package com.cateringplatform.shared.error

data class FieldViolation(
    val field: String,
    val message: String,
)

open class DomainValidationException(val violations: List<FieldViolation>) :
    RuntimeException(violations.joinToString("; ") { "${it.field}: ${it.message}" }) {
    constructor(field: String, message: String) : this(listOf(FieldViolation(field, message)))
}
