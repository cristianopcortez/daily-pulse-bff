package br.com.ccortez.dailypulse.bff.news

import br.com.ccortez.dailypulse.bff.graphql.Aggregator

object AggregatorCatalog {
    const val NEWSAPI_ID = "newsapi"
    const val GNEWS_ID = "gnews"
    const val DEFAULT_ID = NEWSAPI_ID

    private val newsApi = Aggregator(id = NEWSAPI_ID, name = "NewsAPI")
    private val gNews = Aggregator(id = GNEWS_ID, name = "GNews")

    private val all = linkedMapOf(
        NEWSAPI_ID to newsApi,
        GNEWS_ID to gNews,
    )

    fun forIds(configuredIds: Set<String>): List<Aggregator> =
        all.filterKeys { it in configuredIds }.values.toList()

    fun resolveId(aggregatorId: String?): String {
        val trimmed = aggregatorId?.trim().orEmpty()
        return trimmed.ifEmpty { DEFAULT_ID }
    }
}
