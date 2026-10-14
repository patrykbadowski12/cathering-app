package com.cateringplatform

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment
import tools.jackson.databind.json.JsonMapper
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Path

@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    properties = [
        "springdoc.writer-with-default-pretty-printer=true",
        "springdoc.writer-with-order-by-keys=true",
    ],
)
@Import(TestcontainersConfiguration::class)
class OpenApiContractTest(@Autowired private val environment: Environment) {

    private val specFile = Path.of(System.getProperty("openapi.spec"))
    private val jsonMapper = JsonMapper.builder().build()

    @Test
    fun `published OpenAPI spec matches the API`() {
        val actual = fetchApiDocs()

        if (System.getProperty("openapi.update") == "true") {
            Files.createDirectories(specFile.parent)
            Files.writeString(specFile, actual.trimEnd() + "\n")
            return
        }

        assertEquals(
            jsonMapper.readTree(Files.readString(specFile)),
            jsonMapper.readTree(actual),
            "docs/api/openapi.json is out of date. Regenerate it: ./gradlew :app:test --tests '*OpenApiContractTest' -Dopenapi.update=true",
        )
    }

    private fun fetchApiDocs(): String {
        val port = environment.getRequiredProperty("local.server.port")
        val request = HttpRequest.newBuilder(URI.create("http://localhost:$port/v3/api-docs")).GET().build()
        val response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString())
        assertEquals(200, response.statusCode())
        return response.body()
    }
}
