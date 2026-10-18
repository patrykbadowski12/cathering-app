package com.cateringplatform.diet.web

import com.cateringplatform.diet.application.InMemoryDietPlanRepository
import com.cateringplatform.diet.domain.DietPlan
import com.cateringplatform.shared.tenant.TenantContext
import com.cateringplatform.shared.tenant.TenantId
import com.jayway.jsonpath.JsonPath
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.endsWith
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.util.UUID

@WebMvcTest
@Import(DietPlanAdminController::class, DietPlanExceptionHandler::class, DietWebTestConfiguration::class)
class DietPlanAdminControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val repository: InMemoryDietPlanRepository,
) {
    private val tenantId = TenantId(UUID.randomUUID())
    private val otherTenantId = TenantId(UUID.randomUUID())

    @BeforeEach
    fun setTenant() {
        TenantContext.set(tenantId)
    }

    @AfterEach
    fun clearTenant() {
        TenantContext.clear()
    }

    private fun createDietPlan(body: String): ResultActionsDsl = mockMvc.post("/api/v1/admin/diet-plans") {
        contentType = MediaType.APPLICATION_JSON
        content = body
    }

    @Test
    fun `should create diet plan and return its location`() {
        // when
        val result = createDietPlan("""{"name": "Keto", "kcal": 1500}""").andExpect {
            status { isCreated() }
            jsonPath("$.name") { value("Keto") }
            jsonPath("$.kcal") { value(1500) }
            jsonPath("$.state") { value("ACTIVE") }
            jsonPath("$.tenantId") { doesNotExist() }
        }.andReturn()

        // then
        val id = JsonPath.read<String>(result.response.contentAsString, "$.id")
        assertThat(result.response.getHeader("Location"), endsWith("/api/v1/admin/diet-plans/$id"))
        assertEquals(id, repository.findAllByTenantId(tenantId).single().id.value.toString())
    }

    @Test
    fun `should reject invalid diet plan`() {
        createDietPlan("""{"name": " ", "kcal": 0}""").andExpect { status { isBadRequest() } }
        createDietPlan("{}").andExpect { status { isBadRequest() } }
    }

    @Test
    fun `should return own diet plan`() {
        // given
        val dietPlan = repository.save(DietPlan.create(tenantId, "Wege", 1800))

        // when & then
        mockMvc.get("/api/v1/admin/diet-plans/${dietPlan.id.value}").andExpect {
            status { isOk() }
            jsonPath("$.id") { value(dietPlan.id.value.toString()) }
            jsonPath("$.name") { value("Wege") }
            jsonPath("$.tenantId") { doesNotExist() }
        }
    }

    @Test
    fun `should not return diet plan of another tenant`() {
        // given
        val foreignDietPlan = repository.save(DietPlan.create(otherTenantId, "Sport", 2500))

        // when & then
        mockMvc.get("/api/v1/admin/diet-plans/${foreignDietPlan.id.value}").andExpect {
            status { isNotFound() }
            content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
            jsonPath("$.detail") { value("Diet plan ${foreignDietPlan.id.value} not found") }
        }
    }

    @Test
    fun `should return not found for unknown diet plan`() {
        mockMvc.get("/api/v1/admin/diet-plans/${UUID.randomUUID()}").andExpect {
            status { isNotFound() }
            content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
        }
    }

    @Test
    fun `should reject malformed id`() {
        mockMvc.get("/api/v1/admin/diet-plans/abc").andExpect { status { isBadRequest() } }
    }
}
