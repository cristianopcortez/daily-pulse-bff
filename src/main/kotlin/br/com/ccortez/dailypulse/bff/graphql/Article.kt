package br.com.ccortez.dailypulse.bff.graphql

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription(
    "Article card on the Articles screen. " +
        "`date` is ISO-8601 UTC (example: 2024-01-15T12:00:00Z); the KMP client formats relative text."
)
data class Article(
    val title: String,
    val desc: String,
    val date: String,
    val imageUrl: String,
)
