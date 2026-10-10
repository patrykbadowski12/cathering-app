package com.catering_platform.tenant

import com.catering_platform.shared.TenantId

data class ResolvedTenant(
    val id: TenantId,
    val slug: String
)