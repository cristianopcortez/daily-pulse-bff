package br.com.ccortez.dailypulse.bff.news

import kotlin.test.Test
import kotlin.test.assertEquals

class AggregatorCatalogTest {
    @Test
    fun `available returns newsapi entry`() {
        val aggregators = AggregatorCatalog.available()
        assertEquals(1, aggregators.size)
        assertEquals("newsapi", aggregators.single().id)
        assertEquals("NewsAPI", aggregators.single().name)
    }
}
