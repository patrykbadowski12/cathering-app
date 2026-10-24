package com.cateringplatform.diet.domain

import com.cateringplatform.shared.id.Ids
import com.cateringplatform.shared.money.Money

class DietVariant private constructor(
    val id: DietVariantId,
    val kcal: Int,
    price: Money,
    state: DietState,
) {
    var price: Money = price
        private set
    var state: DietState = state
        private set

    companion object {
        fun create(kcal: Int, price: Money): DietVariant {
            if (kcal <= 0) throw InvalidDietException("kcal", "Kcal must be greater than zero!")
            validatePrice(price)
            return DietVariant(DietVariantId(Ids.uuidV7()), kcal, price, DietState.ACTIVE)
        }

        private fun validatePrice(price: Money) {
            if (price.amount <= 0) throw InvalidDietException("price", "Price must be greater than zero!")
        }
    }

    internal fun activate() {
        state = DietState.ACTIVE
    }

    internal fun deactivate() {
        state = DietState.INACTIVE
    }

    internal fun changePrice(newPrice: Money) {
        validatePrice(newPrice)
        price = newPrice
    }
}
