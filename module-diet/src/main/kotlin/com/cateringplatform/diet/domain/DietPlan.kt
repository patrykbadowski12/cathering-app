package com.cateringplatform.diet.domain

import com.cateringplatform.shared.id.Ids
import com.cateringplatform.shared.tenant.TenantId
import java.util.UUID

data class DietPlanId(val value: UUID)

data class DietPlan(
    val id: DietPlanId,
    val name: String,
    val tenantId: TenantId,
    val state: DietPlanState,
    val kcal: Int,
) {
    companion object {
        const val MAX_NAME_LENGTH = 200

        fun create(tenantId: TenantId, name: String, kcal: Int): DietPlan {
            require(name.isNotBlank()) { "Diet plan name must not be blank" }
            require(name.length <= MAX_NAME_LENGTH) { "Diet plan name must be at most $MAX_NAME_LENGTH characters" }
            require(kcal > 0) { "Diet plan kcal must be greater than zero" }
            return DietPlan(
                id = DietPlanId(Ids.uuidV7()),
                name = name,
                kcal = kcal,
                state = DietPlanState.ACTIVE,
                tenantId = tenantId,
            )
        }
    }
}
