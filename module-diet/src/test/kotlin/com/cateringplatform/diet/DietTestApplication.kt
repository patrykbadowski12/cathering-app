package com.cateringplatform.diet

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.AutoConfigurationPackage
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.util.Optional

@SpringBootConfiguration
@AutoConfigurationPackage
@EnableJpaAuditing(auditorAwareRef = "testAuditorAware")
class DietTestApplication {
    @Bean
    fun testAuditorAware(): AuditorAware<String> = AuditorAware { Optional.of(TEST_AUDITOR) }

    companion object {
        const val TEST_AUDITOR = "test-user"
    }
}
