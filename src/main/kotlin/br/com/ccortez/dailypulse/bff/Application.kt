package br.com.ccortez.dailypulse.bff

import br.com.ccortez.dailypulse.bff.graphql.AggregatorsQuery
import br.com.ccortez.dailypulse.bff.graphql.ArticlesQuery
import br.com.ccortez.dailypulse.bff.graphql.SafeDataFetcherExceptionHandler
import br.com.ccortez.dailypulse.bff.graphql.SourcesQuery
import br.com.ccortez.dailypulse.bff.news.NewsProvider
import br.com.ccortez.dailypulse.bff.news.newsapi.NewsApiClient
import br.com.ccortez.dailypulse.bff.news.newsapi.NewsApiProvider
import com.expediagroup.graphql.server.ktor.GraphQL
import com.expediagroup.graphql.server.ktor.defaultGraphQLStatusPages
import com.expediagroup.graphql.server.ktor.graphQLPostRoute
import com.expediagroup.graphql.server.ktor.graphiQLRoute
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.module(newsProvider: NewsProvider? = null) {
    val ownedClient = if (newsProvider == null) createNewsHttpClient() else null
    val provider = newsProvider ?: NewsApiProvider(
        NewsApiClient(
            httpClient = ownedClient!!,
            apiKeyProvider = { Env.newsApiKey },
        ),
    )

    if (ownedClient != null) {
        monitor.subscribe(ApplicationStopped) {
            ownedClient.close()
        }
    }

    install(StatusPages) {
        defaultGraphQLStatusPages()
    }

    install(GraphQL) {
        schema {
            packages = listOf("br.com.ccortez.dailypulse.bff")
            queries = listOf(
                AggregatorsQuery(),
                ArticlesQuery(provider),
                SourcesQuery(provider),
            )
        }
        engine {
            exceptionHandler = SafeDataFetcherExceptionHandler()
        }
    }

    routing {
        get("/health") {
            call.respondText("ok", status = HttpStatusCode.OK)
        }
        graphQLPostRoute()
        if (developmentMode) {
            graphiQLRoute()
        }
    }
}

internal fun createNewsHttpClient(): HttpClient = HttpClient(CIO) {
    expectSuccess = false
    install(HttpTimeout) {
        requestTimeoutMillis = 10_000
        connectTimeoutMillis = 5_000
        socketTimeoutMillis = 10_000
    }
}
