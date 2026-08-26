package br.com.ccortez.dailypulse.bff.news

import br.com.ccortez.dailypulse.bff.graphql.Article
import br.com.ccortez.dailypulse.bff.graphql.Source
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.runBlocking

class AggregatorRouterTest {
    private val fakeProvider = object : NewsProvider {
        override suspend fun articles(sourceId: String?): List<Article> = emptyList()
        override suspend fun sources(): List<Source> = emptyList()
    }

    @Test
    fun `defaults to newsapi provider`() = runBlocking<Unit> {
        val router = AggregatorRouter(mapOf(AggregatorCatalog.NEWSAPI_ID to fakeProvider))
        router.sources(null)
        router.sources("")
        router.sources("  ")
    }

    @Test
    fun `routes explicit newsapi id`() = runBlocking<Unit> {
        val router = AggregatorRouter(mapOf(AggregatorCatalog.NEWSAPI_ID to fakeProvider))
        router.sources(AggregatorCatalog.NEWSAPI_ID)
    }

    @Test
    fun `unknown aggregator throws`() {
        val router = AggregatorRouter(mapOf(AggregatorCatalog.NEWSAPI_ID to fakeProvider))
        val error = assertFailsWith<NewsSourceException.UnknownAggregator> {
            runBlocking { router.articles("gnews", null) }
        }
        assertEquals("News aggregator is not available: gnews", error.message)
    }
}
