package com.cateringplatform.web

import com.cateringplatform.shared.error.DomainValidationException
import com.cateringplatform.shared.error.FieldViolation
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice
class ApiExceptionHandler : ResponseEntityExceptionHandler() {

    private val log = LoggerFactory.getLogger(ApiExceptionHandler::class.java)

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val violations = ex.bindingResult.fieldErrors
            .map { FieldViolation(it.field, it.defaultMessage ?: "Invalid value") }
        return ResponseEntity.badRequest().body(validationProblem(violations))
    }

    @ExceptionHandler(DomainValidationException::class)
    fun handleDomainValidation(ex: DomainValidationException): ProblemDetail = validationProblem(ex.violations)

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception): ProblemDetail {
        log.error("Unexpected error while handling request", ex)
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error")
    }

    private fun validationProblem(violations: List<FieldViolation>): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed").apply {
            setProperty(ERRORS_PROPERTY, violations)
        }

    companion object {
        const val ERRORS_PROPERTY = "errors"
    }
}
