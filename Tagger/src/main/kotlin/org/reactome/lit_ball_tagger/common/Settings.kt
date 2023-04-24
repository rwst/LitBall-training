package org.reactome.lit_ball_tagger.common

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException
import kotlin.io.path.Path
import kotlin.io.path.isReadable

@Serializable
object Settings {
    private const val path = "settings.json"
    private val Json = Json { prettyPrint = true }
    var map: MutableMap<String, String> = mutableMapOf()
    fun load() {
        val db = Path(path)
        var text = ""
        if (db.isReadable()) {
            try {
                text = File(path).readText()
            }
            catch (e: IOException) { Logger.error(e) }
        }
        map = try {
            kotlinx.serialization.json.Json.decodeFromString<MutableMap<String,String>>(text)
        } catch (e: Exception) {
            mutableMapOf()
        }
    }
    fun save() {
        try {
            val text = Json.encodeToString(map)
            File(path).writeText(text)
        }
        catch (e: IOException) { Logger.error(e) }
    }
    override fun toString(): String {
        return "Settings()=$map"
    }
}
