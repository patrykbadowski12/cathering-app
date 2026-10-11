package com.catering_platform.tenant

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "catering.tenant")
data class TenantProperties(val default: DefaultTenantProperties) {
    init {
        require(
            value = default.slug.matches(Regex("[a-z0-9-]+")),
            lazyMessage = { "Invalid tenant slug '${default.slug}': expected [a-z0-9-]+" },
        )
    }
}
