package com.catering_platform.diet.web

import com.catering_platform.diet.application.CreateDietPlanUseCase
import com.catering_platform.diet.application.GetDietPlanUseCase
import com.catering_platform.diet.application.ListDietPlanUseCase
import com.catering_platform.diet.domain.DietPlan
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
    val kcal: Int
)

@RestController
@RequestMapping("/api/v1/diet-plans")
class DietPlanController(
    private val createDietPlanUseCase: CreateDietPlanUseCase,
    private val listDietPlanUseCase: ListDietPlanUseCase,
    private val getDietPlanUseCase: GetDietPlanUseCase
) {

    companion object {
        // todo T-004
        val TEMP_TENANT_ID = UUID.fromString("6e8da2d0-5d92-4f4f-af95-97c0b93b8d1f")
    }

    @PostMapping
    fun create(
        @RequestBody request: CreateDietPlanRequest,
        uriComponentsBuilder: UriComponentsBuilder
    ) =
        createDietPlanUseCase.execute(TEMP_TENANT_ID, request.name, request.kcal)
            .let { DietPlanResponse.fromDomain(it) }
            .let { ResponseEntity
                .created(uriComponentsBuilder
                    .path("/api/v1/diet-plans/{id}")
                    .buildAndExpand(it.id)
                    .toUri()
                ).body(it)
            }

    @GetMapping
    fun list() =
        listDietPlanUseCase.execute(TEMP_TENANT_ID)
            .map { DietPlanResponse.fromDomain(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun get(@PathVariable("id" ) id: UUID): ResponseEntity<DietPlanResponse> =
        getDietPlanUseCase.execute(id, TEMP_TENANT_ID)
            ?.let { DietPlanResponse.fromDomain(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
}