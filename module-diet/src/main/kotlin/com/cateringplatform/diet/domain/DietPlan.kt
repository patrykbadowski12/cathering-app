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
            if (name.isBlank()) {
                throw InvalidDietPlanException("name", "Diet plan name must not be blank")
            }
            if (name.length > MAX_NAME_LENGTH) {
                throw InvalidDietPlanException("name", "Diet plan name must be at most $MAX_NAME_LENGTH characters")
            }
            if (kcal <= 0) {
                throw InvalidDietPlanException("kcal", "Diet plan kcal must be greater than zero")
            }
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
