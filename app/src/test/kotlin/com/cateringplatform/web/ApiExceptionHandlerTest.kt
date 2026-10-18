package com.cateringplatform.web

import com.cateringplatform.shared.error.DomainValidationException
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

class ApiExceptionHandlerTest {

    @RestController
    class FailingController {
        @GetMapping("/domain-validation")
        fun domainValidation(): String = throw DomainValidationException("name", "Diet plan name is already taken")

        @GetMapping("/unexpected")
        fun unexpected(): String = throw IllegalStateException("connection string with password")
    }

    private val mockMvc = MockMvcBuilders.standaloneSetup(FailingController())
        .setControllerAdvice(ApiExceptionHandler())
        .build()

    @Test
    fun `should map domain validation exception to field errors`() {
        mockMvc.get("/domain-validation").andExpect {
            status { isBadRequest() }
            content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
            jsonPath("$.errors[0].field") { value("name") }
            jsonPath("$.errors[0].message") { value("Diet plan name is already taken") }
        }
    }

    @Test
    fun `should hide details of unexpected errors`() {
        mockMvc.get("/unexpected").andExpect {
            status { isInternalServerError() }
            content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
            jsonPath("$.detail") { value("Unexpected error") }
            content { string(not(containsString("password"))) }
        }
    }
}
