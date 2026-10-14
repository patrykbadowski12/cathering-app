package com.cateringplatform.architecture

import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

private const val ROOT = "com.cateringplatform"

private val LANGUAGE_PACKAGES = arrayOf("java..", "kotlin..", "org.jetbrains.annotations..")

private val MODULE_INTERNALS = Regex("""^${Regex.escape(ROOT)}\.([^.]+)\.(application|infrastructure|web)(\..*)?$""")

private fun moduleOf(javaClass: JavaClass): String? = javaClass.packageName
    .removePrefix("$ROOT.")
    .takeIf { it != javaClass.packageName }
    ?.substringBefore('.')

class ArchitectureTest {

    private val classes: JavaClasses = importedClasses

    @Test
    fun `domain depends only on the shared kernel and the language`() {
        classes()
            .that().resideInAPackage("..domain..")
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage("..domain..", "$ROOT.shared..", *LANGUAGE_PACKAGES)
            .because("the domain is pure Kotlin (ADR 0005)")
            .check(classes)
    }

    @Test
    fun `application depends only on the domain and spring-tx`() {
        classes()
            .that().resideInAPackage("..application..")
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage(
                "..domain..",
                "..application..",
                "$ROOT.shared..",
                "org.springframework.transaction..",
                *LANGUAGE_PACKAGES,
            )
            .because("use cases may only add the transaction boundary (ADR 0009)")
            .check(classes)
    }

    @Test
    fun `web does not use infrastructure and infrastructure does not use web`() {
        noClasses()
            .that().resideInAPackage("..web..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
            .because("controllers go through use cases (ADR 0009)")
            .check(classes)
        noClasses()
            .that().resideInAPackage("..infrastructure..")
            .should().dependOnClassesThat().resideInAPackage("..web..")
            .check(classes)
    }

    @Test
    fun `modules use other modules only through their domain`() {
        classes()
            .that().resideInAPackage("$ROOT..")
            .should(notAccessInternalsOfOtherModules())
            .because("other modules are reachable only via their domain API and domain events (ADR 0004)")
            .check(classes)
    }

    @Test
    fun `shared kernel does not depend on modules or frameworks`() {
        classes()
            .that().resideInAPackage("$ROOT.shared..")
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage("$ROOT.shared..", "com.github.f4b6a3..", *LANGUAGE_PACKAGES)
            .because("the shared kernel is small, stable and pure Kotlin")
            .check(classes)
    }

    private fun notAccessInternalsOfOtherModules() = object : ArchCondition<JavaClass>(
        "not access application, infrastructure or web of another module",
    ) {
        override fun check(item: JavaClass, events: ConditionEvents) {
            val sourceModule = moduleOf(item)
            item.directDependenciesFromSelf
                .filter { dependency ->
                    val targetModule = MODULE_INTERNALS.find(dependency.targetClass.packageName)?.groupValues?.get(1)
                    targetModule != null && targetModule != sourceModule
                }
                .forEach { dependency -> events.add(SimpleConditionEvent.violated(dependency, dependency.description)) }
        }
    }

    private companion object {
        val importedClasses: JavaClasses = ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .importPackages(ROOT)
    }
}
