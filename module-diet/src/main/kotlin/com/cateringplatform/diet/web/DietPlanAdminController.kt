package com.cateringplatform.diet.web

import com.cateringplatform.diet.application.CreateDietPlanUseCase
import com.cateringplatform.diet.application.GetDietPlanUseCase
import com.cateringplatform.diet.domain.DietPlanId
import com.cateringplatform.shared.tenant.TenantContext
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder
import java.util.UUID

data class CreateDietPlanRequest(
    val name: String,
    val kcal: Int,
)

@RestController
@RequestMapping("/api/v1/admin/diet-plans")
@Tag(name = "DietPlanAdmin", description = "Diet plan management for the catering owner")
class DietPlanAdminController(
    private val createDietPlanUseCase: CreateDietPlanUseCase,
    private val getDietPlanUseCase: GetDietPlanUseCase,
) {

    private val logger = LoggerFactory.getLogger(DietPlanAdminController::class.java)

    @ApiResponse(responseCode = "201", description = "Diet plan created")
    @PostMapping
    fun createAdminDietPlan(
        @RequestBody request: CreateDietPlanRequest,
        uriComponentsBuilder: UriComponentsBuilder,
    ): ResponseEntity<DietPlanResponse> {
        val response = createDietPlanUseCase.execute(TenantContext.get(), request.name, request.kcal)
            .let { DietPlanResponse.fromDomain(it) }
        logger.info("Created dietPlan {}", response)
        val location = uriComponentsBuilder
            .path("/api/v1/admin/diet-plans/{id}")
            .buildAndExpand(response.id)
            .toUri()
        return ResponseEntity.created(location).body(response)
    }

    @ApiResponse(responseCode = "200", description = "Diet plan")
    @ApiResponse(responseCode = "404", description = "Diet plan not found", content = [Content()])
    @GetMapping("/{id}")
    fun getAdminDietPlan(@PathVariable("id") id: UUID): ResponseEntity<DietPlanResponse> =
        getDietPlanUseCase.execute(DietPlanId(id), TenantContext.get())
            ?.let { DietPlanResponse.fromDomain(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
}
