package com.catering_platform.diet.infrastructure

import com.catering_platform.diet.application.CreateDietPlanUseCase
import com.catering_platform.diet.application.GetDietPlanUseCase
import com.catering_platform.diet.domain.DietPlanRepository
import com.catering_platform.diet.application.ListDietPlanUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DietModuleConfig {

    @Bean
    fun createDietPlanUseCase(repository: DietPlanRepository) =
        CreateDietPlanUseCase(repository)

    @Bean fun listDietPlanUseCase(repository: DietPlanRepository) =
        ListDietPlanUseCase(repository)

    @Bean fun getDietPlanUseCase(repository: DietPlanRepository) =
        GetDietPlanUseCase(repository)
}