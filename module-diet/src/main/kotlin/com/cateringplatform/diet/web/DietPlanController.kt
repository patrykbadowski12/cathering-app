package com.cateringplatform.diet.web

import com.cateringplatform.diet.application.CreateDietPlanUseCase
import com.cateringplatform.diet.application.GetDietPlanUseCase
import com.cateringplatform.diet.application.ListDietPlanUseCase
import com.cateringplatform.diet.domain.DietPlanId
import com.cateringplatform.shared.tenant.TenantContext
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
@RequestMapping("/api/v1/diet-plans")
class DietPlanController(
    private val createDietPlanUseCase: CreateDietPlanUseCase,
    private val listDietPlanUseCase: ListDietPlanUseCase,
    private val getDietPlanUseCase: GetDietPlanUseCase,
) {

    private val logger = LoggerFactory.getLogger(DietPlanController::class.java)

    @PostMapping
    fun create(@RequestBody request: CreateDietPlanRequest, uriComponentsBuilder: UriComponentsBuilder) =
        createDietPlanUseCase.execute(TenantContext.get(), request.name, request.kcal)
            .let { DietPlanResponse.fromDomain(it) }
            .also { logger.info("Created dietPlan {}", it) }
            .let {
                ResponseEntity
                    .created(
                        uriComponentsBuilder
                            .path("/api/v1/diet-plans/{id}")
                            .buildAndExpand(it.id)
                            .toUri(),
                    ).body(it)
            }

    @GetMapping
    fun list() = listDietPlanUseCase.execute(TenantContext.get())
        .map { DietPlanResponse.fromDomain(it) }
        .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun get(@PathVariable("id") id: UUID): ResponseEntity<DietPlanResponse> =
        getDietPlanUseCase.execute(DietPlanId(id), TenantContext.get())
            ?.let { DietPlanResponse.fromDomain(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
}
