package com.cateringplatform

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["com.cateringplatform"])
@EnableJpaRepositories(basePackages = ["com.cateringplatform"])
@EntityScan(basePackages = ["com.cateringplatform"])
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@ConfigurationPropertiesScan
class CateringPlatformApplication

fun main(args: Array<String>) {
    runApplication<CateringPlatformApplication>(*args)
}
