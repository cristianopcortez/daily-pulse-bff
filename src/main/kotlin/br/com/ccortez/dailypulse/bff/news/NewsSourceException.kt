package br.com.ccortez.dailypulse.bff.news

sealed class NewsSourceException(message: String) : RuntimeException(message) {
    class Unauthorized : NewsSourceException("News source is not authorized")
    class QuotaExceeded : NewsSourceException("News source quota exceeded")
    class Timeout : NewsSourceException("News source timed out")
    class Unavailable : NewsSourceException("News source is unavailable")
    class UnknownAggregator(aggregatorId: String) :
        NewsSourceException("News aggregator is not available: $aggregatorId")
}
