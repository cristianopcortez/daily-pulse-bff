package br.com.ccortez.dailypulse.bff.graphql

import br.com.ccortez.dailypulse.bff.news.AggregatorCatalog
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query

@Suppress("unused") // invoked by graphql-kotlin via reflection
class AggregatorsQuery : Query {

    @GraphQLDescription("News aggregators available for the app provider selector.")
    fun aggregators(): List<Aggregator> = AggregatorCatalog.available()
}
