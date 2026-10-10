package com.catering_platform.tenant

import java.util.UUID

data class DefaultTenantProperties(
    val id: UUID,
    val slug: String
)
