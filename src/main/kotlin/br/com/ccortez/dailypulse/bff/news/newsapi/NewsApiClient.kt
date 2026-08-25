package br.com.ccortez.dailypulse.bff.news.newsapi

import br.com.ccortez.dailypulse.bff.news.NewsSourceException
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import java.io.IOException

class NewsApiClient(
    private val httpClient: HttpClient,
    private val apiKeyProvider: () -> String?,
    private val baseUrl: String = "https://newsapi.org/v2",
    private val json: Json = Json { ignoreUnknownKeys = true },
) {
    suspend fun topHeadlines(sourceId: String?): NewsApiArticlesResponse {
        val key = requireApiKey()
        val trimmedSource = sourceId?.trim()?.takeIf { it.isNotEmpty() }
        return request {
            httpClient.get("$baseUrl/top-headlines") {
                header("X-Api-Key", key)
                if (trimmedSource != null) {
                    parameter("sources", trimmedSource)
                } else {
                    parameter("country", "us")
                    parameter("category", "business")
                }
            }
        }
    }

    suspend fun sources(): NewsApiSourcesResponse {
        val key = requireApiKey()
        return request {
            httpClient.get("$baseUrl/sources") {
                header("X-Api-Key", key)
            }
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

        if (!response.status.isSuccess() || isErrorPayload(bodyText)) {
            throw mapHttpError(response.status, bodyText)
        }

        return try {
            json.decodeFromString<T>(bodyText)
        } catch (_: Exception) {
            throw NewsSourceException.Unavailable()
        }
    }

    private fun isErrorPayload(bodyText: String): Boolean {
        val status = runCatching { json.decodeFromString<NewsApiErrorBody>(bodyText).status }
            .getOrNull()
        return status.equals("error", ignoreCase = true)
    }

    private fun mapHttpError(status: HttpStatusCode, bodyText: String): NewsSourceException {
        val code = runCatching { json.decodeFromString<NewsApiErrorBody>(bodyText).code }
            .getOrNull()
            ?.lowercase()
        return when {
            status == HttpStatusCode.Unauthorized ||
                status == HttpStatusCode.Forbidden ||
                code == "apikeyinvalid" ||
                code == "apikeymissing" ||
                code == "apikeydisabled" -> NewsSourceException.Unauthorized()
            status == HttpStatusCode.TooManyRequests ||
                code == "apikeyexhausted" ||
                code == "ratelimited" -> NewsSourceException.QuotaExceeded()
            else -> NewsSourceException.Unavailable()
        }
    }
}
