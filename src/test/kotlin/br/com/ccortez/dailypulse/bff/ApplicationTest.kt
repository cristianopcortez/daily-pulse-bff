package br.com.ccortez.dailypulse.bff

import br.com.ccortez.dailypulse.bff.graphql.Article
import br.com.ccortez.dailypulse.bff.graphql.Source
import br.com.ccortez.dailypulse.bff.news.NewsProvider
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {
    private val fakeProvider = object : NewsProvider {
        override suspend fun articles(sourceId: String?): List<Article> = listOf(
            Article(
                title = "Markets open higher",
                desc = "Click to find out more",
                date = "2024-01-15T12:00:00Z",
                imageUrl = "https://image.example/a.jpg",
            ),
        )

        override suspend fun sources(): List<Source> = listOf(
            Source(
                id = "bbc-news",
                name = "BBC News",
                desc = "World news",
                origin = "us - en",
            ),
        )
    }

    @Test
    fun health() = testApplication {
        application { module(fakeProvider) }
        val response = client.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("ok", response.bodyAsText())
    }

    @Test
    fun articlesQuery() = testApplication {
        application { module(fakeProvider) }
        val response = client.post("/graphql") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(
                """{"query":"query Articles(${'$'}source: String) { articles(source: ${'$'}source) { title desc date imageUrl } }","variables":{}}""",
            )
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("Markets open higher"))
        assertTrue(body.contains("Click to find out more"))
        assertTrue(body.contains("2024-01-15T12:00:00Z"))
        assertTrue(body.contains("https://image.example/a.jpg"))
    }

    @Test
    fun aggregatorsQuery() = testApplication {
        application { module(fakeProvider) }
        val response = client.post("/graphql") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(
                """{"query":"query Aggregators { aggregators { id name } }"}""",
            )
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("newsapi"))
        assertTrue(body.contains("NewsAPI"))
    }

    @Test
    fun sourcesQuery() = testApplication {
        application { module(fakeProvider) }
        val response = client.post("/graphql") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(
                """{"query":"query Sources { sources { id name desc origin } }"}""",
            )
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("bbc-news"))
        assertTrue(body.contains("us - en"))
    }
}
