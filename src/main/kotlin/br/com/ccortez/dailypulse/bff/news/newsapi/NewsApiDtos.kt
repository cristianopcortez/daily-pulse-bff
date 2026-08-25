package br.com.ccortez.dailypulse.bff.news.newsapi

import kotlinx.serialization.Serializable

@Serializable
data class NewsApiErrorBody(
    val status: String? = null,
    val code: String? = null,
    val message: String? = null,
)

@Serializable
data class NewsApiArticlesResponse(
    val status: String,
    val code: String? = null,
    val articles: List<NewsApiArticleDto> = emptyList(),
)

@Serializable
data class NewsApiArticleDto(
    val title: String? = null,
    val description: String? = null,
    val publishedAt: String? = null,
    val urlToImage: String? = null,
)

@Serializable
data class NewsApiSourcesResponse(
    val status: String,
    val code: String? = null,
    val sources: List<NewsApiSourceDto> = emptyList(),
)

@Serializable
data class NewsApiSourceDto(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val country: String? = null,
    val language: String? = null,
)
