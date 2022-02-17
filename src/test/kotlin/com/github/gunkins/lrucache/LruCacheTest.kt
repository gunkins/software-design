package com.github.gunkins.lrucache

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LruCacheTest {

    @Test
    fun `Put and get one element`() =
        with(getIntCache(1)) {
            put(5, 5)

            assertEquals(5, get(5))
        }

    @Test
    fun `Overwrite existing element`() =
        with(getIntCache(2)) {
            put(1, 5)
            put(1, 1)

            assertEquals(1, get(1))
        }

    @Test
    fun `Pop out long-inserted elements, capacity=1`() =
        with(getIntCache(1)) {
            put(1, 1)
            put(2, 2)
            put(3, 3)

            assertNull(get(1))
            assertNull(get(2))
            assertEquals(3, get(3))
        }

    @Test
    fun `Pop out long-inserted elements, capacity=3`() {
        val elements = (1..10).toList()
        val capacity = 3
        val cache = getIntCache(capacity)

        elements.forEach { cache.put(it, it) }

        for (popped in elements.take(elements.size - capacity)) {
            assertNull(cache.get(popped))
        }

        for (rest in elements.takeLast(capacity)) {
            assertEquals(rest, cache.get(rest))
        }
    }

    @Test
    fun `Pop out exactly least recently used elements`() =
        with(getIntCache(4)) {
            put(1, 1)
            put(2, 2)
            put(3, 3)
            put(4, 4)

            assertEquals(3, get(3))
            assertEquals(2, get(2))
            assertEquals(1, get(1))

            put(5, 5)
            put(6, 6)

            assertNull(get(3))
            assertNull(get(4))
            assertEquals(1, get(1))
            assertEquals(5, get(5))
            assertEquals(6, get(6))
        }

    private fun getIntCache(capacity: Int) = LruCache<Int, Int>(capacity)
}