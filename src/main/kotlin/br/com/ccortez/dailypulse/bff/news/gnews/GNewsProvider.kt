package br.com.ccortez.dailypulse.bff.news.gnews

import br.com.ccortez.dailypulse.bff.graphql.Article
import br.com.ccortez.dailypulse.bff.graphql.Source
import br.com.ccortez.dailypulse.bff.news.NewsProvider
import br.com.ccortez.dailypulse.bff.news.mapArticle
import br.com.ccortez.dailypulse.bff.news.mapSource
import java.time.Clock

class GNewsProvider(
    private val client: GNewsClient,
    private val clock: Clock = Clock.systemUTC(),
) : NewsProvider {

    override suspend fun articles(sourceId: String?): List<Article> =
        client.topHeadlines(sourceId).articles.mapNotNull { dto ->
            mapArticle(
                title = dto.title,
                description = dto.description,
                publishedAt = dto.publishedAt,
                urlToImage = dto.image,
                clock = clock,
            )
        }

    override suspend fun sources(): List<Source> =
        client.sources().sources.mapNotNull { dto ->
            mapSource(
                id = dto.id,
                name = dto.name,
                description = dto.url,
                country = dto.country,
                language = dto.lang,
            )
        }
}
