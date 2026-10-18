package com.cateringplatform.diet.web

import com.cateringplatform.diet.application.DietPlanNotFoundException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackageClasses = [DietPlanExceptionHandler::class])
class DietPlanExceptionHandler {

    @ExceptionHandler(DietPlanNotFoundException::class)
    fun handleDietPlanNotFound(ex: DietPlanNotFoundException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message)
}
