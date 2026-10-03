package com.catering_platform.diet.infrastructure

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DietPlanJpaRepository : JpaRepository<DietPlanEntity, UUID>