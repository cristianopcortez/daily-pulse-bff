package br.com.ccortez.dailypulse.bff.news.gnews

import br.com.ccortez.dailypulse.bff.news.NewsSourceException
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import java.io.IOException

class GNewsClient(
    private val httpClient: HttpClient,
    private val apiKeyProvider: () -> String?,
    private val baseUrl: String = "https://gnews.io/api/v4",
    private val json: Json = Json { ignoreUnknownKeys = true },
) {
    suspend fun topHeadlines(sourceId: String?): GNewsArticlesResponse {
        val key = requireApiKey()
        val trimmedSource = sourceId?.trim()?.takeIf { it.isNotEmpty() }
        val maxArticles = if (trimmedSource == null) DEFAULT_MAX else SOURCE_FILTER_MAX
        val response = request<GNewsArticlesResponse> {
            httpClient.get("$baseUrl/top-headlines") {
                parameter("apikey", key)
                parameter("category", "business")
                parameter("lang", "en")
                parameter("country", "us")
                parameter("max", maxArticles)
            }
        }
        if (trimmedSource == null) return response
        return response.copy(
            articles = response.articles.filter { article ->
                article.source?.id.equals(trimmedSource, ignoreCase = true)
            },
        )
    }

    suspend fun sources(): GNewsSourcesResponse = request {
        httpClient.get("$baseUrl/sources") {
            parameter("apikey", requireApiKey())
            parameter("lang", "en")
        }
    }

    private fun requireApiKey(): String {
        val key = apiKeyProvider()?.trim().orEmpty()
        if (key.isEmpty()) throw NewsSourceException.Unauthorized()
        return key
    }

    private suspend inline fun <reified T> request(block: suspend () -> HttpResponse): T {
        val response = try {
            block()
        } catch (_: HttpRequestTimeoutException) {
            throw NewsSourceException.Timeout()
        } catch (_: IOException) {
            throw NewsSourceException.Unavailable()
        }

        val bodyText = try {
            response.bodyAsText()
        } catch (_: Exception) {
            throw NewsSourceException.Unavailable()
        }

        if (!response.status.isSuccess()) {
            throw mapHttpError(response.status)
        }

        return try {
            json.decodeFromString<T>(bodyText)
        } catch (_: Exception) {
            throw NewsSourceException.Unavailable()
        }
    }

    private fun mapHttpError(status: HttpStatusCode): NewsSourceException = when (status) {
        HttpStatusCode.Unauthorized,
        HttpStatusCode.Forbidden,
        -> NewsSourceException.Unauthorized()
        HttpStatusCode.TooManyRequests -> NewsSourceException.QuotaExceeded()
        else -> NewsSourceException.Unavailable()
    }

    private companion object {
        const val DEFAULT_MAX = 10
        const val SOURCE_FILTER_MAX = 100
    }
}
