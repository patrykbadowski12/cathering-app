package com.cateringplatform.diet.web

import com.cateringplatform.diet.application.GetDietPlanUseCase
import com.cateringplatform.diet.application.ListDietPlanUseCase
import com.cateringplatform.diet.domain.DietPlanId
import com.cateringplatform.shared.tenant.TenantContext
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/shop/diet-plans")
@Tag(name = "DietPlanShop", description = "Diet plans offered to customers")
class DietPlanShopController(
    private val listDietPlanUseCase: ListDietPlanUseCase,
    private val getDietPlanUseCase: GetDietPlanUseCase,
) {

    @ApiResponse(responseCode = "200", description = "Diet plans of the current tenant")
    @GetMapping
    fun listShopDietPlans(): ResponseEntity<List<DietPlanResponse>> = listDietPlanUseCase.execute(TenantContext.get())
        .map { DietPlanResponse.fromDomain(it) }
        .let { ResponseEntity.ok(it) }

    @ApiResponse(responseCode = "200", description = "Diet plan")
    @ApiResponse(
        responseCode = "404",
        description = "Diet plan not found",
        content = [Content(mediaType = "application/problem+json", schema = Schema(ref = PROBLEM_DETAIL_SCHEMA))],
    )
    @GetMapping("/{id}")
    fun getShopDietPlan(@PathVariable("id") id: UUID): ResponseEntity<DietPlanResponse> =
        getDietPlanUseCase.execute(DietPlanId(id), TenantContext.get())
            .let { DietPlanResponse.fromDomain(it) }
            .let { ResponseEntity.ok(it) }
}
