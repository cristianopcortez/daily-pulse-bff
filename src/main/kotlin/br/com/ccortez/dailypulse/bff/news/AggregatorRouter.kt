package br.com.ccortez.dailypulse.bff.news

import br.com.ccortez.dailypulse.bff.graphql.Article
import br.com.ccortez.dailypulse.bff.graphql.Source

class AggregatorRouter(
    private val providers: Map<String, NewsProvider>,
) {
    suspend fun articles(aggregatorId: String?, sourceId: String?): List<Article> =
        providerFor(aggregatorId).articles(sourceId)

    suspend fun sources(aggregatorId: String?): List<Source> =
        providerFor(aggregatorId).sources()

    private fun providerFor(aggregatorId: String?): NewsProvider {
        val resolvedId = AggregatorCatalog.resolveId(aggregatorId)
        return providers[resolvedId] ?: throw NewsSourceException.UnknownAggregator(resolvedId)
    }
}
