package com.cateringplatform.diet.infrastructure

import com.cateringplatform.diet.domain.DietPlan
import com.cateringplatform.diet.domain.DietPlanId
import com.cateringplatform.diet.domain.DietPlanState
import com.cateringplatform.shared.tenant.TenantId
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.PostLoad
import jakarta.persistence.PostPersist
import jakarta.persistence.Table
import jakarta.persistence.Transient
import org.springframework.data.domain.Persistable
import java.util.UUID

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
) : AuditableEntity(),
    Persistable<UUID> {

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
        fun fromDomain(dietPlan: DietPlan): DietPlanEntity = DietPlanEntity(
            id = dietPlan.id.value,
            tenantId = dietPlan.tenantId.value,
            name = dietPlan.name,
            kcal = dietPlan.kcal,
            state = dietPlan.state,
        )
    }

    fun toDietPlan(): DietPlan = DietPlan(
        id = DietPlanId(id),
        name = name,
        kcal = kcal,
        state = state,
        tenantId = TenantId(tenantId),
    )
}
