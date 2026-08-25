package br.com.ccortez.dailypulse.bff.news

import br.com.ccortez.dailypulse.bff.graphql.Article
import br.com.ccortez.dailypulse.bff.graphql.Source

/**
 * First-party news contract for GraphQL. Additional REST sources implement this
 * and can be composed later without changing Article/Source types.
 */
interface NewsProvider {
    suspend fun articles(sourceId: String?): List<Article>
    suspend fun sources(): List<Source>
}
