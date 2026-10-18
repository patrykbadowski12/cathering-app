package com.cateringplatform.diet

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.util.Optional

@TestConfiguration(proxyBeanMethods = false)
@EnableJpaAuditing(auditorAwareRef = "testAuditorAware")
class JpaAuditingTestConfiguration {
    @Bean
    fun testAuditorAware(): AuditorAware<String> = AuditorAware { Optional.of(TEST_AUDITOR) }

    companion object {
        const val TEST_AUDITOR = "test-user"
    }
}
