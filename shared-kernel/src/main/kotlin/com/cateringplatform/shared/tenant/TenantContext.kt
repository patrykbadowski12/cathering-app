package com.cateringplatform.shared.tenant

object TenantContext {
    private val current = ThreadLocal<TenantId>()
    fun set(id: TenantId) = current.set(id)
    fun get(): TenantId = current.get() ?: throw NoTenantException()
    fun clear() = current.remove()
}
