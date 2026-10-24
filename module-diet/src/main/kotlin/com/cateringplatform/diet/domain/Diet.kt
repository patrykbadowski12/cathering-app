package com.cateringplatform.diet.domain

import com.cateringplatform.shared.id.Ids
import com.cateringplatform.shared.money.Money
import com.cateringplatform.shared.tenant.TenantId
import java.util.UUID

data class DietId(val value: UUID)

class Diet(
    val id: DietId,
    val tenantId: TenantId,
    name: String,
    state: DietState,
    val slug: String,
    description: String,
    dietVariants: List<DietVariant>,
) {
    var name: String = name
        private set
    var state: DietState = state
        private set
    var description: String = description
        private set
    private val _dietVariants: MutableList<DietVariant> = dietVariants.toMutableList()

    val dietVariants: List<DietVariant>
        get() = _dietVariants.toList()

    companion object {
        const val MAX_NAME_LENGTH = 200
        const val MAX_DESCRIPTION_LENGTH = 2000

        fun create(
            tenantId: TenantId,
            name: String,
            description: String,
            variantPrice: Money,
            variantKcal: Int,
        ): Diet {
            validateNameField(name)
            validateDescriptionField(description)

            return Diet(
                id = DietId(Ids.uuidV7()),
                tenantId = tenantId,
                name = name,
                state = DietState.ACTIVE,
                slug = Slug.toSlug(name),
                description = description,
                dietVariants = listOf(DietVariant.create(variantKcal, variantPrice)),
            )
        }

        private fun validateNameField(name: String) {
            if (name.isBlank()) {
                throw InvalidDietException("name", "Diet name must not be blank")
            }
            if (name.length > MAX_NAME_LENGTH) {
                throw InvalidDietException("name", "Diet name must be at most $MAX_NAME_LENGTH characters")
            }
            if (Slug.toSlug(name).isEmpty()) {
                throw InvalidDietException("name", "Diet name must contain at least one letter or digit")
            }
        }

        private fun validateDescriptionField(description: String) {
            if (description.isBlank()) {
                throw InvalidDietException("description", "Diet description must not be blank")
            }
            if (description.length > MAX_DESCRIPTION_LENGTH) {
                throw InvalidDietException(
                    "description",
                    "Diet description must be at most $MAX_DESCRIPTION_LENGTH characters",
                )
            }
        }
    }

    fun updateName(name: String) {
        validateNameField(name)
        this.name = name
    }

    fun updateDescription(description: String) {
        validateDescriptionField(description)
        this.description = description
    }

    fun addVariant(kcal: Int, price: Money) {
        if (_dietVariants.any { it.kcal == kcal }) {
            throw InvalidDietException("kcal", "Diet variant with kcal $kcal already exists!")
        }
        this._dietVariants.add(DietVariant.create(kcal, price))
    }

    fun activateVariant(dietVariantId: DietVariantId) {
        val dietVariant =
            dietVariants.find { it.id == dietVariantId } ?: throw DietVariantNotFoundException(dietVariantId)

        dietVariant.activate()
    }

    fun deactivateVariant(dietVariantId: DietVariantId) {
        val dietVariant =
            dietVariants.find { it.id == dietVariantId } ?: throw DietVariantNotFoundException(dietVariantId)

        val activeVariants = dietVariants.filter { it.state == DietState.ACTIVE }
        if (state == DietState.ACTIVE && activeVariants.size == 1 && activeVariants.first().id == dietVariantId) {
            throw InvalidDietException("dietVariants", "Cannot deactivate last active variant of active diet!")
        }

        dietVariant.deactivate()
    }

    fun deactivate() {
        state = DietState.INACTIVE
    }

    fun activate() {
        if (dietVariants.none { it.state == DietState.ACTIVE }) {
            throw InvalidDietException(
                "dietVariants",
                "Cannot activate diet with no active variant!",
            )
        }
        state = DietState.ACTIVE
    }

    fun changeVariantPrice(dietVariantId: DietVariantId, newPrice: Money) {
        val dietVariant =
            dietVariants.find { it.id == dietVariantId } ?: throw DietVariantNotFoundException(dietVariantId)
        dietVariant.changePrice(newPrice)
    }
}
