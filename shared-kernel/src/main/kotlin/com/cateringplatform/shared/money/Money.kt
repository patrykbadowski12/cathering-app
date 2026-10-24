package com.cateringplatform.shared.money

import com.cateringplatform.shared.error.DomainValidationException

/**
 * Money amount in minor units (grosze). 1 PLN = 100.
 */
@JvmInline
value class Money(val amount: Long) : Comparable<Money> {

    init {
        if (amount < 0) throw DomainValidationException("amount", "Money amount must not be negative")
    }

    override fun compareTo(other: Money) = amount.compareTo(other.amount)
}
