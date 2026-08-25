package br.com.ccortez.dailypulse.bff.graphql

import br.com.ccortez.dailypulse.bff.news.NewsProvider
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query

@Suppress("unused") // invoked by graphql-kotlin via reflection
class ArticlesQuery(
    private val newsProvider: NewsProvider,
) : Query {

    @GraphQLDescription(
        "Articles for the Articles screen. " +
            "Omit `source` for US business top headlines; pass a source id (example: bbc-news) to filter."
    )
    suspend fun articles(
        @GraphQLDescription("NewsAPI source id. When omitted, country=us and category=business.")
        source: String? = null,
    ): List<Article> = newsProvider.articles(source)
}
