package com.catering_platform.diet.infrastructure

import com.catering_platform.diet.domain.DietPlan
import com.catering_platform.diet.domain.DietPlanId
import com.catering_platform.diet.domain.DietPlanState
import jakarta.persistence.*
import org.springframework.data.domain.Persistable
import java.util.*

@Entity
@Table(name = "diet_plans")
class DietPlanEntity(
    @Id
    private val id: UUID,
    val tenantId: UUID,
    val name: String,
    val kcal: Int,
    @Enumerated(EnumType.STRING)
    val state: DietPlanState,
) : AuditableEntity(), Persistable<UUID> {

    @Transient
    private var newEntity: Boolean = true

    override fun getId(): UUID = id
    override fun isNew(): Boolean = newEntity

    @PostPersist
    @PostLoad
    private fun markNotNew() {
        newEntity = false
    }
    companion object {
        fun fromDomain(dietPlan: DietPlan): DietPlanEntity =
            DietPlanEntity(
                id = dietPlan.id.value,
                tenantId = dietPlan.tenantId,
                name = dietPlan.name,
                kcal = dietPlan.kcal,
                state = dietPlan.state
            )
    }

    fun toDietPlan(): DietPlan {
        return DietPlan(
            id = DietPlanId(id),
            name = name,
            kcal = kcal,
            state = state,
            tenantId = tenantId
        )
    }
}