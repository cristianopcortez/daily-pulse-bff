package br.com.ccortez.dailypulse.bff

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv

object Env {
    private val dotenv: Dotenv = dotenv {
        ignoreIfMissing = true
    }

    fun get(key: String): String? {
        val fromFile = dotenv[key]?.trim()?.takeIf { it.isNotEmpty() }
        val fromProcess = System.getenv(key)?.trim()?.takeIf { it.isNotEmpty() }
        return fromProcess ?: fromFile
    }

    val newsApiKey: String?
        get() = get("NEWS_API_KEY")

    val gnewsApiKey: String?
        get() = get("GNEWS_API_KEY")
}
