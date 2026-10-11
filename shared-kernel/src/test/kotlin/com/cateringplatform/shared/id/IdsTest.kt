package com.cateringplatform.shared.id

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class IdsTest {

    @Test
    fun `should generate version 7 uuid`() {
        // when
        val id = Ids.uuidV7()

        // then
        assertEquals(7, id.version())
    }

    @Test
    fun `should generate ids in ascending order`() {
        // when
        val ids = List(1_000) { Ids.uuidV7() }

        // then
        assertEquals(ids.sorted(), ids)
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `should keep creation order when compared as strings`() {
        // given: Postgres sorts uuid columns byte by byte, which matches the order of the text form
        val first = Ids.uuidV7()
        val second = Ids.uuidV7()

        // then
        assertTrue(first.toString() < second.toString())
    }
}
