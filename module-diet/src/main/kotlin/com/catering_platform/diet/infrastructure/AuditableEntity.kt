package com.catering_platform.diet.infrastructure

import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class AuditableEntity {
    @CreatedDate
    var createdOn: Instant? = null
        protected set

    @CreatedBy
    var createdBy: String? = null
        protected set

    @LastModifiedDate
    var modifiedOn: Instant? = null
        protected set

    @LastModifiedBy
    var modifiedBy: String? = null
        protected set
}