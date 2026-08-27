package br.com.ccortez.dailypulse.bff.news.gnews

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

class GNewsClientTest {
    @Test
    fun `top headlines without source uses us business defaults`() = runBlocking<Unit> {
        var capturedUrl = ""
        val client = GNewsClient(
            httpClient = HttpClient(MockEngine) {
                engine {
                    addHandler { request ->
                        capturedUrl = request.url.toString()
                        respond(
                            content = """{"totalArticles":1,"articles":[{"title":"Hello","description":null,"publishedAt":"2024-01-15T12:00:00Z","image":null,"source":{"id":"abc","name":"Example"}}]}""",
                            status = HttpStatusCode.OK,
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    }
                }
            },
            apiKeyProvider = { "test-key" },
        )
        val response = client.topHeadlines(null)
        assertEquals(1, response.articles.size)
        assertTrue(capturedUrl.contains("category=business"))
        assertTrue(capturedUrl.contains("country=us"))
        assertTrue(capturedUrl.contains("lang=en"))
        assertTrue(capturedUrl.contains("max=10"))
        assertTrue(capturedUrl.contains("apikey="))
    }

    @Test
    fun `top headlines with source filters by source id`() = runBlocking<Unit> {
        val client = GNewsClient(
            httpClient = HttpClient(MockEngine) {
                engine {
                    addHandler {
                        respond(
                            content = """
                                {
                                  "totalArticles": 2,
                                  "articles": [
                                    {"title":"Match","description":null,"publishedAt":"2024-01-15T12:00:00Z","image":null,"source":{"id":"target-source","name":"Target"}},
                                    {"title":"Other","description":null,"publishedAt":"2024-01-15T12:00:00Z","image":null,"source":{"id":"other","name":"Other"}}
                                  ]
                                }
                            """.trimIndent(),
                            status = HttpStatusCode.OK,
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    }
                }
            },
            apiKeyProvider = { "test-key" },
        )
        val response = client.topHeadlines("target-source")
        assertEquals(1, response.articles.size)
        assertEquals("Match", response.articles.single().title)
    }

    @Test
    fun `unauthorized is mapped without leaking details`() = runBlocking<Unit> {
        val client = GNewsClient(
            httpClient = HttpClient(MockEngine) {
                engine {
                    addHandler {
                        respond(
                            content = """{"errors":["Invalid API key"]}""",
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
        val client = GNewsClient(
            httpClient = HttpClient(MockEngine) {
                engine {
                    addHandler {
                        respond(
                            content = """{"errors":["Too many requests"]}""",
                            status = HttpStatusCode.TooManyRequests,
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    }
                }
            },
            apiKeyProvider = { "test-key" },
        )
        assertFailsWith<NewsSourceException.QuotaExceeded> {
            client.topHeadlines(null)
        }
    }
}
