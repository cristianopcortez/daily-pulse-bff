package br.com.ccortez.dailypulse.bff.news

import br.com.ccortez.dailypulse.bff.graphql.Article
import br.com.ccortez.dailypulse.bff.graphql.Source
import java.time.Clock
import java.time.Instant
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

object ArticleDefaults {
    const val DESC_FALLBACK = "Click to find out more"
    const val IMAGE_FALLBACK =
        "https://image.cnbcfm.com/api/v1/image/107326078-1698758530118-gettyimages-1765623456-wall26362_igj6ehhp.jpeg?v=1698758587&w=1920&h=1080"
}

fun mapArticle(
    title: String?,
    description: String?,
    publishedAt: String?,
    urlToImage: String?,
    clock: Clock = Clock.systemUTC(),
): Article? {
    val resolvedTitle = title?.trim().orEmpty()
    if (resolvedTitle.isEmpty()) return null
    return Article(
        title = resolvedTitle,
        desc = normalizeDesc(description),
        date = normalizePublishedAt(publishedAt, clock),
        imageUrl = normalizeImageUrl(urlToImage),
    )
}

fun mapSource(
    id: String?,
    name: String?,
    description: String?,
    country: String?,
    language: String?,
): Source? {
    val resolvedId = id?.trim().orEmpty()
    val resolvedName = name?.trim().orEmpty()
    if (resolvedId.isEmpty() || resolvedName.isEmpty()) return null
    return Source(
        id = resolvedId,
        name = resolvedName,
        desc = description?.trim().orEmpty(),
        origin = origin(country, language),
    )
}

fun normalizeDesc(description: String?): String {
    val value = description?.trim().orEmpty()
    return value.ifEmpty { ArticleDefaults.DESC_FALLBACK }
}

fun normalizeImageUrl(urlToImage: String?): String {
    val value = urlToImage?.trim().orEmpty()
    return if (isHttpUrl(value)) value else ArticleDefaults.IMAGE_FALLBACK
}

fun normalizePublishedAt(raw: String?, clock: Clock): String {
    val value = raw?.trim().orEmpty()
    if (value.isEmpty()) return Instant.now(clock).truncatedTo(ChronoUnit.SECONDS).toString()
    return parseInstant(value)?.truncatedTo(ChronoUnit.SECONDS)?.toString()
        ?: Instant.now(clock).truncatedTo(ChronoUnit.SECONDS).toString()
}

fun origin(country: String?, language: String?): String {
    val countryPart = country?.trim().orEmpty().ifEmpty { "unknown" }
    val languagePart = language?.trim().orEmpty().ifEmpty { "unknown" }
    return "$countryPart - $languagePart"
}

private fun isHttpUrl(value: String): Boolean =
    value.startsWith("http://", ignoreCase = true) || value.startsWith("https://", ignoreCase = true)

private fun parseInstant(value: String): Instant? = runCatching {
    Instant.parse(value)
}.getOrElse {
    runCatching { OffsetDateTime.parse(value).toInstant() }.getOrNull()
}
