package br.com.ccortez.dailypulse.bff.news

import kotlin.test.Test
import kotlin.test.assertEquals

class AggregatorCatalogTest {
    @Test
    fun `forIds returns only configured aggregators`() {
        val aggregators = AggregatorCatalog.forIds(setOf(AggregatorCatalog.NEWSAPI_ID))
        assertEquals(1, aggregators.size)
        assertEquals("newsapi", aggregators.single().id)
        assertEquals("NewsAPI", aggregators.single().name)
    }

    @Test
    fun `forIds includes gnews when configured`() {
        val aggregators = AggregatorCatalog.forIds(
            setOf(AggregatorCatalog.NEWSAPI_ID, AggregatorCatalog.GNEWS_ID),
        )
        assertEquals(2, aggregators.size)
        assertEquals(listOf("newsapi", "gnews"), aggregators.map { it.id })
    }

    @Test
    fun `resolveId defaults to newsapi`() {
        assertEquals(AggregatorCatalog.DEFAULT_ID, AggregatorCatalog.resolveId(null))
        assertEquals(AggregatorCatalog.DEFAULT_ID, AggregatorCatalog.resolveId(""))
        assertEquals(AggregatorCatalog.DEFAULT_ID, AggregatorCatalog.resolveId("  "))
    }

    @Test
    fun `resolveId keeps explicit value`() {
        assertEquals("gnews", AggregatorCatalog.resolveId("gnews"))
    }
}
