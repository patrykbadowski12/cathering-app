package com.catering_platform.diet.infrastructure

import com.catering_platform.diet.domain.DietPlanState
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "diet_plans")
class DietPlanEntity(
    @Id val id: UUID,
    val tenantId: UUID,
    val name: String,
    val kcal: Int,
    @Enumerated(EnumType.STRING)
    val state: DietPlanState,
): AuditableEntity()