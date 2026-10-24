package com.cateringplatform.diet.domain

import com.cateringplatform.shared.error.DomainValidationException
import com.cateringplatform.shared.money.Money
import com.cateringplatform.shared.tenant.TenantId
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.ThrowableAssert.ThrowingCallable
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.UUID

class DietTest {

    private val tenantId = TenantId(UUID.randomUUID())

    private fun createDiet(
        name: String = "Keto",
        description: String = "Low carb diet",
        kcal: Int = 1500,
        price: Money = Money(5990),
    ) = Diet.create(tenantId, name, description, variantPrice = price, variantKcal = kcal)

    private fun Diet.variant(kcal: Int) = dietVariants.single { it.kcal == kcal }

    private fun assertViolation(field: String, call: ThrowingCallable) {
        assertThatThrownBy(call)
            .isInstanceOf(InvalidDietException::class.java)
            .satisfies({
                assertThat((it as DomainValidationException).violations)
                    .extracting("field")
                    .containsExactly(field)
            })
    }

    @Nested
    inner class Create {

        @Test
        fun `new diet is active with one active variant`() {
            val diet = createDiet(kcal = 1500, price = Money(5990))

            assertThat(diet.tenantId).isEqualTo(tenantId)
            assertThat(diet.name).isEqualTo("Keto")
            assertThat(diet.description).isEqualTo("Low carb diet")
            assertThat(diet.state).isEqualTo(DietState.ACTIVE)
            assertThat(diet.dietVariants).singleElement().satisfies({
                assertThat(it.kcal).isEqualTo(1500)
                assertThat(it.price).isEqualTo(Money(5990))
                assertThat(it.state).isEqualTo(DietState.ACTIVE)
            })
        }

        @Test
        fun `each diet gets its own id`() {
            assertThat(createDiet().id).isNotEqualTo(createDiet().id)
        }

        @ParameterizedTest
        @ValueSource(strings = ["", "   "])
        fun `rejects blank name`(name: String) {
            assertViolation("name") { createDiet(name = name) }
        }

        @Test
        fun `accepts name of max length`() {
            val name = "a".repeat(Diet.MAX_NAME_LENGTH)

            assertThat(createDiet(name = name).name).isEqualTo(name)
        }

        @Test
        fun `rejects too long name`() {
            assertViolation("name") { createDiet(name = "a".repeat(Diet.MAX_NAME_LENGTH + 1)) }
        }

        @ParameterizedTest
        @ValueSource(strings = ["", "   "])
        fun `rejects blank description`(description: String) {
            assertViolation("description") { createDiet(description = description) }
        }

        @Test
        fun `rejects too long description`() {
            assertViolation("description") {
                createDiet(description = "a".repeat(Diet.MAX_DESCRIPTION_LENGTH + 1))
            }
        }

        @ParameterizedTest
        @ValueSource(ints = [0, -1])
        fun `rejects non positive kcal`(kcal: Int) {
            assertViolation("kcal") { createDiet(kcal = kcal) }
        }

        @Test
        fun `rejects zero price`() {
            assertViolation("price") { createDiet(price = Money(0)) }
        }
    }

    @Nested
    inner class Slug {

        @ParameterizedTest
        @CsvSource(
            "Keto, keto",
            "Wegańska Fit, weganska-fit",
            "'  Żółć & Łosoś 2000  ', zolc-losos-2000",
            "Dieta--Sport__Max, dieta-sport-max",
        )
        fun `is generated from name`(name: String, expected: String) {
            assertThat(createDiet(name = name).slug).isEqualTo(expected)
        }

        @ParameterizedTest
        @ValueSource(strings = ["!!!", "🔥🔥", "Кето"])
        fun `name without sluggable characters is rejected`(name: String) {
            assertViolation("name") { createDiet(name = name) }
        }

        @Test
        fun `does not change when diet is renamed`() {
            val diet = createDiet(name = "Keto")

            diet.updateName("Low Carb")

            assertThat(diet.slug).isEqualTo("keto")
        }
    }

    @Nested
    inner class Update {

        @Test
        fun `changes name`() {
            val diet = createDiet()

            diet.updateName("Low Carb")

            assertThat(diet.name).isEqualTo("Low Carb")
        }

        @Test
        fun `rejects invalid name and keeps the old one`() {
            val diet = createDiet(name = "Keto")

            assertViolation("name") { diet.updateName(" ") }
            assertViolation("name") { diet.updateName("a".repeat(Diet.MAX_NAME_LENGTH + 1)) }
            assertThat(diet.name).isEqualTo("Keto")
        }

        @Test
        fun `changes description`() {
            val diet = createDiet()

            diet.updateDescription("New description")

            assertThat(diet.description).isEqualTo("New description")
        }

        @Test
        fun `rejects invalid description and keeps the old one`() {
            val diet = createDiet(description = "Low carb diet")

            assertViolation("description") { diet.updateDescription(" ") }
            assertViolation("description") {
                diet.updateDescription("a".repeat(Diet.MAX_DESCRIPTION_LENGTH + 1))
            }
            assertThat(diet.description).isEqualTo("Low carb diet")
        }
    }

    @Nested
    inner class AddVariant {

        @Test
        fun `adds active variant`() {
            val diet = createDiet(kcal = 1500)

            diet.addVariant(kcal = 2000, price = Money(6990))

            assertThat(diet.dietVariants).hasSize(2)
            assertThat(diet.variant(2000).price).isEqualTo(Money(6990))
            assertThat(diet.variant(2000).state).isEqualTo(DietState.ACTIVE)
        }

        @Test
        fun `rejects existing kcal`() {
            val diet = createDiet(kcal = 1500)

            assertViolation("kcal") { diet.addVariant(kcal = 1500, price = Money(6990)) }
            assertThat(diet.dietVariants).hasSize(1)
        }

        @Test
        fun `rejects kcal of inactive variant`() {
            val diet = createDiet(kcal = 1500)
            diet.addVariant(kcal = 2000, price = Money(6990))
            diet.deactivateVariant(diet.variant(1500).id)

            assertViolation("kcal") { diet.addVariant(kcal = 1500, price = Money(5990)) }
        }

        @ParameterizedTest
        @ValueSource(ints = [0, -1])
        fun `rejects non positive kcal`(kcal: Int) {
            assertViolation("kcal") { createDiet().addVariant(kcal = kcal, price = Money(5990)) }
        }

        @Test
        fun `rejects zero price`() {
            assertViolation("price") { createDiet().addVariant(kcal = 2000, price = Money(0)) }
        }
    }

    @Nested
    inner class ChangeVariantPrice {

        @Test
        fun `changes price of variant`() {
            val diet = createDiet(price = Money(5990))
            val variantId = diet.dietVariants.single().id

            diet.changeVariantPrice(variantId, Money(6490))

            assertThat(diet.dietVariants.single().price).isEqualTo(Money(6490))
        }

        @Test
        fun `rejects zero price and keeps the old one`() {
            val diet = createDiet(price = Money(5990))
            val variantId = diet.dietVariants.single().id

            assertViolation("price") { diet.changeVariantPrice(variantId, Money(0)) }
            assertThat(diet.dietVariants.single().price).isEqualTo(Money(5990))
        }

        @Test
        fun `fails for unknown variant`() {
            assertThatThrownBy { createDiet().changeVariantPrice(DietVariantId(UUID.randomUUID()), Money(6490)) }
                .isInstanceOf(DietVariantNotFoundException::class.java)
        }
    }

    @Nested
    inner class VariantState {

        @Test
        fun `deactivates one of active variants`() {
            val diet = createDiet(kcal = 1500)
            diet.addVariant(kcal = 2000, price = Money(6990))

            diet.deactivateVariant(diet.variant(1500).id)

            assertThat(diet.variant(1500).state).isEqualTo(DietState.INACTIVE)
            assertThat(diet.variant(2000).state).isEqualTo(DietState.ACTIVE)
        }

        @Test
        fun `cannot deactivate last active variant of active diet`() {
            val diet = createDiet()

            assertViolation("dietVariants") { diet.deactivateVariant(diet.dietVariants.single().id) }
            assertThat(diet.dietVariants.single().state).isEqualTo(DietState.ACTIVE)
        }

        @Test
        fun `deactivating already inactive variant keeps last active one untouched`() {
            val diet = createDiet(kcal = 1500)
            diet.addVariant(kcal = 2000, price = Money(6990))
            diet.deactivateVariant(diet.variant(2000).id)

            diet.deactivateVariant(diet.variant(2000).id)

            assertThat(diet.variant(1500).state).isEqualTo(DietState.ACTIVE)
            assertThat(diet.variant(2000).state).isEqualTo(DietState.INACTIVE)
        }

        @Test
        fun `can deactivate last active variant of inactive diet`() {
            val diet = createDiet()
            diet.deactivate()

            diet.deactivateVariant(diet.dietVariants.single().id)

            assertThat(diet.dietVariants.single().state).isEqualTo(DietState.INACTIVE)
        }

        @Test
        fun `activates inactive variant`() {
            val diet = createDiet(kcal = 1500)
            diet.addVariant(kcal = 2000, price = Money(6990))
            diet.deactivateVariant(diet.variant(2000).id)

            diet.activateVariant(diet.variant(2000).id)

            assertThat(diet.variant(2000).state).isEqualTo(DietState.ACTIVE)
        }

        @Test
        fun `fails for unknown variant`() {
            val diet = createDiet()
            val unknownId = DietVariantId(UUID.randomUUID())

            assertThatThrownBy { diet.activateVariant(unknownId) }
                .isInstanceOf(DietVariantNotFoundException::class.java)
            assertThatThrownBy { diet.deactivateVariant(unknownId) }
                .isInstanceOf(DietVariantNotFoundException::class.java)
        }
    }

    @Nested
    inner class DietActivation {

        @Test
        fun `deactivates diet and keeps variants as they were`() {
            val diet = createDiet()

            diet.deactivate()

            assertThat(diet.state).isEqualTo(DietState.INACTIVE)
            assertThat(diet.dietVariants.single().state).isEqualTo(DietState.ACTIVE)
        }

        @Test
        fun `activates diet with active variant`() {
            val diet = createDiet()
            diet.deactivate()

            diet.activate()

            assertThat(diet.state).isEqualTo(DietState.ACTIVE)
        }

        @Test
        fun `cannot activate diet without active variant`() {
            val diet = createDiet()
            diet.deactivate()
            diet.deactivateVariant(diet.dietVariants.single().id)

            assertViolation("dietVariants") { diet.activate() }
            assertThat(diet.state).isEqualTo(DietState.INACTIVE)
        }
    }

    @Nested
    inner class Encapsulation {

        @Test
        fun `exposed variant list cannot be used to modify diet`() {
            val diet = createDiet(kcal = 1500)
            diet.addVariant(kcal = 2000, price = Money(6990))

            runCatching { (diet.dietVariants as MutableList<DietVariant>).clear() }

            assertThat(diet.dietVariants).hasSize(2)
        }

        @Test
        fun `variants passed to constructor are copied`() {
            val variants = createDiet().dietVariants.toMutableList()
            val diet = Diet(
                id = DietId(UUID.randomUUID()),
                tenantId = tenantId,
                name = "Keto",
                state = DietState.ACTIVE,
                slug = "keto",
                description = "Low carb diet",
                dietVariants = variants,
            )

            variants.clear()

            assertThat(diet.dietVariants).hasSize(1)
        }
    }
}
