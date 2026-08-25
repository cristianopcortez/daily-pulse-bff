package br.com.ccortez.dailypulse.bff.news

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class NewsMappersTest {
    private val clock = Clock.fixed(Instant.parse("2024-01-15T12:00:00Z"), ZoneOffset.UTC)

    @Test
    fun `desc falls back when null or blank`() {
        val fromNull = mapArticle("Title", null, "2024-01-15T12:00:00Z", "https://img.example/a.jpg", clock)!!
        val fromBlank = mapArticle("Title", "  ", "2024-01-15T12:00:00Z", "https://img.example/a.jpg", clock)!!
        assertEquals(ArticleDefaults.DESC_FALLBACK, fromNull.desc)
        assertEquals(ArticleDefaults.DESC_FALLBACK, fromBlank.desc)
    }

    @Test
    fun `imageUrl falls back when missing or not http`() {
        val fromNull = mapArticle("Title", "d", "2024-01-15T12:00:00Z", null, clock)!!
        val fromRelative = mapArticle("Title", "d", "2024-01-15T12:00:00Z", "/local.png", clock)!!
        assertEquals(ArticleDefaults.IMAGE_FALLBACK, fromNull.imageUrl)
        assertEquals(ArticleDefaults.IMAGE_FALLBACK, fromRelative.imageUrl)
    }

    @Test
    fun `date is ISO-8601 UTC`() {
        val article = mapArticle("Title", "d", "2024-01-15T12:00:00Z", "https://img.example/a.jpg", clock)!!
        assertEquals("2024-01-15T12:00:00Z", article.date)
    }

    @Test
    fun `invalid date uses clock`() {
        val article = mapArticle("Title", "d", "not-a-date", "https://img.example/a.jpg", clock)!!
        assertEquals("2024-01-15T12:00:00Z", article.date)
    }

    @Test
    fun `blank title is dropped`() {
        assertNull(mapArticle("  ", "d", "2024-01-15T12:00:00Z", "https://img.example/a.jpg", clock))
    }

    @Test
    fun `source origin is country dash language`() {
        val source = mapSource("bbc-news", "BBC News", "World news", "us", "en")!!
        assertEquals("bbc-news", source.id)
        assertEquals("BBC News", source.name)
        assertEquals("World news", source.desc)
        assertEquals("us - en", source.origin)
    }
}
