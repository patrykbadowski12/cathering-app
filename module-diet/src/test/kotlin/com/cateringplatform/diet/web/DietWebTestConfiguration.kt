package com.cateringplatform.diet.web

import com.cateringplatform.diet.application.CreateDietPlanUseCase
import com.cateringplatform.diet.application.GetDietPlanUseCase
import com.cateringplatform.diet.application.InMemoryDietPlanRepository
import com.cateringplatform.diet.application.ListDietPlanUseCase
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration(proxyBeanMethods = false)
class DietWebTestConfiguration {
    @Bean
    fun inMemoryDietPlanRepository() = InMemoryDietPlanRepository()

    @Bean
    fun createDietPlanUseCase(repository: InMemoryDietPlanRepository) = CreateDietPlanUseCase(repository)

    @Bean
    fun getDietPlanUseCase(repository: InMemoryDietPlanRepository) = GetDietPlanUseCase(repository)

    @Bean
    fun listDietPlanUseCase(repository: InMemoryDietPlanRepository) = ListDietPlanUseCase(repository)
}
