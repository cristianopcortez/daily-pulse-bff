package br.com.ccortez.dailypulse.bff.news.gnews

import kotlinx.serialization.Serializable

@Serializable
data class GNewsArticlesResponse(
    val totalArticles: Int = 0,
    val articles: List<GNewsArticleDto> = emptyList(),
)

@Serializable
data class GNewsArticleDto(
    val title: String? = null,
    val description: String? = null,
    val publishedAt: String? = null,
    val image: String? = null,
    val source: GNewsSourceRefDto? = null,
)

@Serializable
data class GNewsSourceRefDto(
    val id: String? = null,
    val name: String? = null,
)

@Serializable
data class GNewsSourcesResponse(
    val sources: List<GNewsSourceDto> = emptyList(),
)

@Serializable
data class GNewsSourceDto(
    val id: String? = null,
    val name: String? = null,
    val url: String? = null,
    val country: String? = null,
    val lang: String? = null,
)
