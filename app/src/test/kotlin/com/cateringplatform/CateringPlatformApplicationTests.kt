package com.cateringplatform

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

// TODO(T-007): switch to Testcontainers so the build no longer needs `docker compose up`.
@SpringBootTest
@ActiveProfiles("local")
class CateringPlatformApplicationTests {
    @Test
    fun contextLoads() {
    }
}
