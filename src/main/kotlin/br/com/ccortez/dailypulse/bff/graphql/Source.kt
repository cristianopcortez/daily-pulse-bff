package br.com.ccortez.dailypulse.bff.graphql

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("News source card on the Sources screen. `id` is the NewsAPI source id used to filter articles.")
data class Source(
    val id: String,
    val name: String,
    val desc: String,
    @property:GraphQLDescription("Ready-to-render origin, format `{country} - {language}` (example: us - en).")
    val origin: String,
)
