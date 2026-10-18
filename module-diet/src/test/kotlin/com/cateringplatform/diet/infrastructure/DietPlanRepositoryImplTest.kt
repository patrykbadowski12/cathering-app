package com.cateringplatform.diet.infrastructure

import com.cateringplatform.diet.JpaAuditingTestConfiguration
import com.cateringplatform.diet.TestcontainersConfiguration
import com.cateringplatform.diet.domain.DietPlan
import com.cateringplatform.diet.domain.DietPlanId
import com.cateringplatform.shared.tenant.TenantId
import jakarta.persistence.EntityManager
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import java.util.UUID

@DataJpaTest(properties = ["spring.jpa.properties.hibernate.generate_statistics=true"])
@Import(DietPlanRepositoryImpl::class, TestcontainersConfiguration::class, JpaAuditingTestConfiguration::class)
class DietPlanRepositoryImplTest @Autowired constructor(
    private val repository: DietPlanRepositoryImpl,
    private val jpaRepository: DietPlanJpaRepository,
    private val entityManager: EntityManager,
) {
    private val tenantId = TenantId(UUID.randomUUID())
    private val otherTenantId = TenantId(UUID.randomUUID())

    @Test
    fun `should save diet plan and read it back from database`() {
        // given
        val dietPlan = DietPlan.create(tenantId, "Keto", 1500)

        // when
        repository.save(dietPlan)
        flushAndClear()
        val result = repository.findByIdAndTenantId(dietPlan.id, tenantId)

        // then
        assertEquals(dietPlan, result)
    }

    @Test
    fun `should insert new diet plan without selecting it first`() {
        // given
        val statistics = entityManager.entityManagerFactory.unwrap(SessionFactory::class.java).statistics
        statistics.clear()

        // when
        repository.save(DietPlan.create(tenantId, "Keto", 1500))
        entityManager.flush()

        // then
        assertEquals(1, statistics.prepareStatementCount)
    }

    @Test
    fun `should fill audit columns on insert`() {
        // given
        val dietPlan = DietPlan.create(tenantId, "Keto", 1500)

        // when
        repository.save(dietPlan)
        flushAndClear()
        val entity = jpaRepository.findById(dietPlan.id.value).orElseThrow()

        // then
        assertEquals(JpaAuditingTestConfiguration.TEST_AUDITOR, entity.createdBy)
        assertNotNull(entity.createdOn)
    }

    @Test
    fun `should return only diet plans of requested tenant`() {
        // given
        val keto = repository.save(DietPlan.create(tenantId, "Keto", 1500))
        val vege = repository.save(DietPlan.create(tenantId, "Wege", 1800))
        repository.save(DietPlan.create(otherTenantId, "Sport", 2500))
        flushAndClear()

        // when
        val result = repository.findAllByTenantId(tenantId)

        // then
        assertEquals(setOf(keto.id, vege.id), result.map { it.id }.toSet())
    }

    @Test
    fun `should not find diet plan of another tenant`() {
        // given
        val dietPlan = repository.save(DietPlan.create(otherTenantId, "Sport", 2500))
        flushAndClear()

        // when
        val result = repository.findByIdAndTenantId(dietPlan.id, tenantId)

        // then
        assertNull(result)
    }

    @Test
    fun `should not find unknown diet plan`() {
        // when
        val result = repository.findByIdAndTenantId(DietPlanId(UUID.randomUUID()), tenantId)

        // then
        assertNull(result)
    }

    private fun flushAndClear() {
        entityManager.flush()
        entityManager.clear()
    }
}
