package com.cateringplatform.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.IntegerSchema
import io.swagger.v3.oas.models.media.ObjectSchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.StringSchema
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun openApi(): OpenAPI = OpenAPI()
        .info(Info().title("Catering API").version("1.0.0"))
        .servers(listOf(Server().url("/")))
        .components(
            Components()
                .addSchemas("FieldViolation", fieldViolationSchema())
                .addSchemas("ProblemDetail", problemDetailSchema()),
        )

    @Bean
    fun requiredPropertiesAreNotNullable(): OpenApiCustomizer = OpenApiCustomizer { openApi ->
        openApi.components?.schemas?.values?.forEach { schema ->
            schema.required.orEmpty().forEach { name ->
                val property = schema.properties?.get(name) ?: return@forEach
                property.types = property.types?.minus("null")
            }
        }
    }

    private fun fieldViolationSchema(): Schema<*> = ObjectSchema()
        .addProperty("field", StringSchema())
        .addProperty("message", StringSchema())
        .required(listOf("field", "message"))

    private fun problemDetailSchema(): Schema<*> = ObjectSchema()
        .description("RFC 9457 problem details")
        .addProperty("type", StringSchema())
        .addProperty("title", StringSchema())
        .addProperty("status", IntegerSchema())
        .addProperty("detail", StringSchema())
        .addProperty("instance", StringSchema())
        .addProperty(
            "errors",
            ArraySchema().items(Schema<Any>().`$ref`("#/components/schemas/FieldViolation")),
        )
}
