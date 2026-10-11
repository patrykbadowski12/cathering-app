package com.cateringplatform.tenant

import com.cateringplatform.shared.TenantId

data class ResolvedTenant(
    val id: TenantId,
    val slug: String,
)
