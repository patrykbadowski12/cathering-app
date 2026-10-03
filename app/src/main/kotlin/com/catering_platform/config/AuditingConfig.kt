package com.catering_platform.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import java.util.Optional

@Configuration
class AuditingConfig {
    @Bean
    fun auditorAware(): AuditorAware<String> = AuditorAware {
        Optional.of("TESTUSER") // tymczasowo; docelowo user z Security
    }
}