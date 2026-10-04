package com.catering_platform

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["com.catering_platform"])
@EnableJpaRepositories(basePackages = ["com.catering_platform"])
@EntityScan(basePackages = ["com.catering_platform"])
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
class CateringPlatformApplication

fun main(args: Array<String>) {
	runApplication<CateringPlatformApplication>(*args)
}