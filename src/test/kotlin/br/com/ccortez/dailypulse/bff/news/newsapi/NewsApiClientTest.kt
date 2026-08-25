package br.com.ccortez.dailypulse.bff.news.newsapi

import br.com.ccortez.dailypulse.bff.news.NewsSourceException
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class NewsApiClientTest {
    @Test
    fun `top headlines without source uses us business`() = runBlocking<Unit> {
        var capturedUrl = ""
        val client = NewsApiClient(
            httpClient = HttpClient(MockEngine) {
                engine {
                    addHandler { request ->
                        capturedUrl = request.url.toString()
                        respond(
                            content = """{"status":"ok","articles":[{"title":"Hello","description":null,"publishedAt":"2024-01-15T12:00:00Z","urlToImage":null}]}""",
                            status = HttpStatusCode.OK,
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    }
                }
            },
            apiKeyProvider = { "test-key" },
            baseUrl = "https://newsapi.org/v2",
        )
        val response = client.topHeadlines(null)
        assertEquals("ok", response.status)
        assertEquals(1, response.articles.size)
        assertTrue(capturedUrl.contains("country=us"))
        assertTrue(capturedUrl.contains("category=business"))
        assertTrue(!capturedUrl.contains("apiKey"))
    }

    @Test
    fun `unauthorized is mapped without leaking details`() = runBlocking<Unit> {
        val client = NewsApiClient(
            httpClient = HttpClient(MockEngine) {
                engine {
                    addHandler {
                        respond(
                            content = """{"status":"error","code":"apiKeyInvalid","message":"Your API key is invalid"}""",
                            status = HttpStatusCode.Unauthorized,
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    }
                }
            },
            apiKeyProvider = { "bad-key" },
        )
        val error = assertFailsWith<NewsSourceException.Unauthorized> {
            client.topHeadlines(null)
        }
        assertEquals("News source is not authorized", error.message)
    }

    @Test
    fun `quota is mapped`() = runBlocking<Unit> {
        val client = NewsApiClient(
            httpClient = HttpClient(MockEngine) {
                engine {
                    addHandler {
                        respond(
                            content = """{"status":"error","code":"rateLimited","message":"too many"}""",
                            status = HttpStatusCode.TooManyRequests,
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    }
                }
            },
            apiKeyProvider = { "test-key" },
        )
        assertFailsWith<NewsSourceException.QuotaExceeded> {
            client.topHeadlines("bbc-news")
        }
    }
}
