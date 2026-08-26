package br.com.ccortez.dailypulse.bff.news

import br.com.ccortez.dailypulse.bff.graphql.Aggregator

object AggregatorCatalog {
    const val NEWSAPI_ID = "newsapi"
    const val DEFAULT_ID = NEWSAPI_ID

    private val newsApi = Aggregator(id = NEWSAPI_ID, name = "NewsAPI")

    fun available(): List<Aggregator> = listOf(newsApi)

    fun resolveId(aggregatorId: String?): String {
        val trimmed = aggregatorId?.trim().orEmpty()
        return trimmed.ifEmpty { DEFAULT_ID }
    }
}
