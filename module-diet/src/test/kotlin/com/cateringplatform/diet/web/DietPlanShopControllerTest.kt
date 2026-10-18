package com.cateringplatform.diet.web

import com.cateringplatform.diet.application.InMemoryDietPlanRepository
import com.cateringplatform.diet.domain.DietPlan
import com.cateringplatform.shared.tenant.TenantContext
import com.cateringplatform.shared.tenant.TenantId
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.util.UUID

@WebMvcTest
@Import(DietPlanShopController::class, DietPlanExceptionHandler::class, DietWebTestConfiguration::class)
class DietPlanShopControllerTest @Autowired constructor(
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

    @Test
    fun `should list only diet plans of current tenant`() {
        // given
        repository.save(DietPlan.create(tenantId, "Keto", 1500))
        repository.save(DietPlan.create(tenantId, "Wege", 1800))
        repository.save(DietPlan.create(otherTenantId, "Sport", 2500))

        // when & then
        mockMvc.get("/api/v1/shop/diet-plans").andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(2) }
            jsonPath("$[*].name") { value(containsInAnyOrder("Keto", "Wege")) }
            jsonPath("$[*].tenantId") { isEmpty() }
        }
    }

    @Test
    fun `should return own diet plan`() {
        // given
        val dietPlan = repository.save(DietPlan.create(tenantId, "Keto", 1500))

        // when & then
        mockMvc.get("/api/v1/shop/diet-plans/${dietPlan.id.value}").andExpect {
            status { isOk() }
            jsonPath("$.name") { value("Keto") }
            jsonPath("$.kcal") { value(1500) }
            jsonPath("$.tenantId") { doesNotExist() }
        }
    }

    @Test
    fun `should not return diet plan of another tenant`() {
        // given
        val foreignDietPlan = repository.save(DietPlan.create(otherTenantId, "Sport", 2500))

        // when & then
        mockMvc.get("/api/v1/shop/diet-plans/${foreignDietPlan.id.value}").andExpect {
            status { isNotFound() }
            content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
        }
    }

    @Test
    fun `should return not found for unknown diet plan`() {
        mockMvc.get("/api/v1/shop/diet-plans/${UUID.randomUUID()}").andExpect {
            status { isNotFound() }
            content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
        }
    }

    @Test
    fun `should reject malformed id`() {
        mockMvc.get("/api/v1/shop/diet-plans/abc").andExpect { status { isBadRequest() } }
    }
}
