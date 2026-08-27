package br.com.ccortez.dailypulse.bff.graphql

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query

@Suppress("unused") // invoked by graphql-kotlin via reflection
class AggregatorsQuery(
    private val aggregators: List<Aggregator>,
) : Query {

    @GraphQLDescription("News aggregators available for the app provider selector.")
    fun aggregators(): List<Aggregator> = aggregators
}
