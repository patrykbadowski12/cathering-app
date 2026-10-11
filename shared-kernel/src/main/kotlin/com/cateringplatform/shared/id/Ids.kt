package com.cateringplatform.shared.id

import com.github.f4b6a3.uuid.UuidCreator
import java.util.UUID

object Ids {
    fun uuidV7(): UUID = UuidCreator.getTimeOrderedEpoch()
}
