package br.com.ccortez.dailypulse.bff.graphql

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription(
    "News aggregator available in the app. " +
        "`id` is the stable key sent back to the BFF when provider routing is enabled."
)
data class Aggregator(
    val id: String,
    val name: String,
)
