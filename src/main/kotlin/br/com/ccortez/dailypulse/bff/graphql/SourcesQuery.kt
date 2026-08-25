package br.com.ccortez.dailypulse.bff.graphql

import br.com.ccortez.dailypulse.bff.news.NewsProvider
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query

@Suppress("unused") // invoked by graphql-kotlin via reflection
class SourcesQuery(
    private val newsProvider: NewsProvider,
) : Query {

    @GraphQLDescription("Sources for the Sources screen.")
    suspend fun sources(): List<Source> = newsProvider.sources()
}
