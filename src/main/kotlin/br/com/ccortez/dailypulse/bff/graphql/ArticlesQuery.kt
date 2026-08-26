package br.com.ccortez.dailypulse.bff.graphql

import br.com.ccortez.dailypulse.bff.news.AggregatorCatalog
import br.com.ccortez.dailypulse.bff.news.AggregatorRouter
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query

@Suppress("unused") // invoked by graphql-kotlin via reflection
class ArticlesQuery(
    private val aggregatorRouter: AggregatorRouter,
) : Query {

    @GraphQLDescription(
        "Articles for the Articles screen. " +
            "Omit `source` for US business top headlines; pass a source id (example: bbc-news) to filter."
    )
    suspend fun articles(
        @GraphQLDescription(
            "News aggregator id (example: newsapi). When omitted, defaults to `${AggregatorCatalog.DEFAULT_ID}`."
        )
        aggregator: String? = null,
        @GraphQLDescription("News source id within the selected aggregator. When omitted, country=us and category=business.")
        source: String? = null,
    ): List<Article> = aggregatorRouter.articles(aggregator, source)
}
