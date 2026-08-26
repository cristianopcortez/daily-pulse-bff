package br.com.ccortez.dailypulse.bff.graphql

import br.com.ccortez.dailypulse.bff.news.AggregatorCatalog
import br.com.ccortez.dailypulse.bff.news.AggregatorRouter
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query

@Suppress("unused") // invoked by graphql-kotlin via reflection
class SourcesQuery(
    private val aggregatorRouter: AggregatorRouter,
) : Query {

    @GraphQLDescription("Sources for the Sources screen.")
    suspend fun sources(
        @GraphQLDescription(
            "News aggregator id (example: newsapi). When omitted, defaults to `${AggregatorCatalog.DEFAULT_ID}`."
        )
        aggregator: String? = null,
    ): List<Source> = aggregatorRouter.sources(aggregator)
}
