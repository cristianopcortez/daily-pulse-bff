package br.com.ccortez.dailypulse.bff.news.newsapi

import br.com.ccortez.dailypulse.bff.graphql.Article
import br.com.ccortez.dailypulse.bff.graphql.Source
import br.com.ccortez.dailypulse.bff.news.NewsProvider
import br.com.ccortez.dailypulse.bff.news.NewsSourceException
import br.com.ccortez.dailypulse.bff.news.mapArticle
import br.com.ccortez.dailypulse.bff.news.mapSource
import java.time.Clock

class NewsApiProvider(
    private val client: NewsApiClient,
    private val clock: Clock = Clock.systemUTC(),
) : NewsProvider {

    override suspend fun articles(sourceId: String?): List<Article> {
        val response = client.topHeadlines(sourceId)
        if (response.status.equals("error", ignoreCase = true)) {
            throw NewsSourceException.Unavailable()
        }
        return response.articles.mapNotNull { dto ->
            mapArticle(
                title = dto.title,
                description = dto.description,
                publishedAt = dto.publishedAt,
                urlToImage = dto.urlToImage,
                clock = clock,
            )
        }
    }

    override suspend fun sources(): List<Source> {
        val response = client.sources()
        if (response.status.equals("error", ignoreCase = true)) {
            throw NewsSourceException.Unavailable()
        }
        return response.sources.mapNotNull { dto ->
            mapSource(
                id = dto.id,
                name = dto.name,
                description = dto.description,
                country = dto.country,
                language = dto.language,
            )
        }
    }
}
