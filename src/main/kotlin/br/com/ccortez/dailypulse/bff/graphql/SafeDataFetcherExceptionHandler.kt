package br.com.ccortez.dailypulse.bff.graphql

import br.com.ccortez.dailypulse.bff.news.NewsSourceException
import graphql.GraphqlErrorBuilder
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture

class SafeDataFetcherExceptionHandler : DataFetcherExceptionHandler {
    private val log = LoggerFactory.getLogger(SafeDataFetcherExceptionHandler::class.java)

    override fun handleException(
        handlerParameters: DataFetcherExceptionHandlerParameters,
    ): CompletableFuture<DataFetcherExceptionHandlerResult> {
        val cause = unwrap(handlerParameters.exception)
        val message = if (cause is NewsSourceException) {
            cause.message ?: "News source is unavailable"
        } else {
            log.error("Unhandled GraphQL data fetcher error", cause)
            "Unable to load data"
        }
        val error = GraphqlErrorBuilder.newError()
            .message(message)
            .path(handlerParameters.path)
            .location(handlerParameters.sourceLocation)
            .build()
        return CompletableFuture.completedFuture(
            DataFetcherExceptionHandlerResult.newResult().error(error).build(),
        )
    }

    private fun unwrap(throwable: Throwable): Throwable {
        var current = throwable
        while (current.cause != null && current.cause !== current && current !is NewsSourceException) {
            current = current.cause!!
        }
        return current
    }
}
