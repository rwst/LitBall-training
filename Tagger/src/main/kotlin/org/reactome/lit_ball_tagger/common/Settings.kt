package org.reactome.lit_ball_tagger.common

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException

@Serializable
object Settings {
    private const val PATH = "settings.json"
    private val Json = Json { prettyPrint = true }
    var map: MutableMap<String, String> = mutableMapOf()
    fun load() {
        val file = File(PATH)
        if (file.canRead()) {
            try {
                val text = file.readText()
                map = Json.decodeFromString(text)
            } catch (e: IOException) {
                Logger.error(e)
            } catch (e: SerializationException) {
                Logger.error(e)
                map = mutableMapOf()
            }
        }
    }

    fun save() {
        try {
            val text = Json.encodeToString(map)
            File(PATH).writeText(text)
        } catch (e: IOException) {
            Logger.error(e)
        } catch (e: SerializationException) {
            Logger.error(e)
        }
    }

    override fun toString(): String {
        return "Settings()=$map"
    }
}
