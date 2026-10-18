package com.cateringplatform.web

import com.cateringplatform.TestcontainersConfiguration
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration::class)
class DietPlanApiErrorsTest(@Autowired private val mockMvc: MockMvc) {

    private fun createDietPlan(body: String): ResultActionsDsl = mockMvc.post("/api/v1/admin/diet-plans") {
        contentType = MediaType.APPLICATION_JSON
        content = body
    }

    @Test
    fun `should return field errors for invalid values`() {
        createDietPlan("""{"name": " ", "kcal": 0}""").andExpect {
            status { isBadRequest() }
            content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
            jsonPath("$.status") { value(400) }
            jsonPath("$.errors.length()") { value(2) }
            jsonPath("$.errors[?(@.field == 'name')].message") { value("must not be blank") }
            jsonPath("$.errors[?(@.field == 'kcal')].message") { value("must be greater than 0") }
        }
    }

    @Test
    fun `should return field error for too long name`() {
        createDietPlan("""{"name": "${"a".repeat(201)}", "kcal": 1500}""").andExpect {
            status { isBadRequest() }
            jsonPath("$.errors[0].field") { value("name") }
        }
    }

    @Test
    fun `should return field errors for missing fields`() {
        createDietPlan("{}").andExpect {
            status { isBadRequest() }
            content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
            jsonPath("$.errors[?(@.field == 'name')]") { exists() }
            jsonPath("$.errors[?(@.field == 'kcal')]") { exists() }
        }
    }

    @Test
    fun `should return problem detail for malformed json`() {
        createDietPlan("""{"name":""").andExpect {
            status { isBadRequest() }
            content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
            jsonPath("$.errors") { doesNotExist() }
        }
    }

    @Test
    fun `should return not found problem for unknown diet plan`() {
        val id = UUID.randomUUID()

        listOf("/api/v1/admin/diet-plans/$id", "/api/v1/shop/diet-plans/$id").forEach { path ->
            mockMvc.get(path).andExpect {
                status { isNotFound() }
                content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
                jsonPath("$.detail") { value("Diet plan $id not found") }
            }
        }
    }
}
