package com.catering_platform.shared

object TenantContext {
    private val current = ThreadLocal<TenantId>()
    fun set(id: TenantId) = current.set(id)
    fun get(): TenantId = current.get() ?: throw NoTenantException()
    fun clear() = current.remove()
}
