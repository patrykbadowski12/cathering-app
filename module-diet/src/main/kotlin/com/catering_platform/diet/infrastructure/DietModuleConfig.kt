package com.catering_platform.diet.infrastructure

import com.catering_platform.diet.domain.CreateDietPlanUseCase
import com.catering_platform.diet.domain.DietPlanRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DietModuleConfig {

    @Bean
    fun createDietPlanUseCase(repository: DietPlanRepository) =
        CreateDietPlanUseCase(repository)
}