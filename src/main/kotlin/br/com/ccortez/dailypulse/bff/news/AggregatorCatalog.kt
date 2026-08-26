package br.com.ccortez.dailypulse.bff.news

import br.com.ccortez.dailypulse.bff.graphql.Aggregator

object AggregatorCatalog {
    const val NEWSAPI_ID = "newsapi"
    const val DEFAULT_ID = NEWSAPI_ID

    private val newsApi = Aggregator(id = NEWSAPI_ID, n