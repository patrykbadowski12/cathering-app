package com.cateringplatform.diet.infrastructure

import com.cateringplatform.diet.application.CreateDietPlanUseCase
import com.cateringplatform.diet.application.GetDietPlanUseCase
import com.cateringplatform.diet.application.ListDietPlanUseCase
import com.cateringplatform.diet.domain.DietPlanRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DietModuleConfig {

    @Bean
    fun createDietPlanUseCase(repository: DietPlanRepository) = CreateDietPlanUseCase(repository)

    @Bean fun listDietPlanUseCase(repository: DietPlanRepository) = ListDietPlanUseCase(repository)

    @Bean fun getDietPlanUseCase(repository: DietPlanRepository) = GetDietPlanUseCase(repository)
}
