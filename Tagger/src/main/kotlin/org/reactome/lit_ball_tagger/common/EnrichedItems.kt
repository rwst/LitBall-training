package org.reactome.lit_ball_tagger.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

object EnrichedItems {
    var initialized = false
        private set
    private val map = mutableMapOf<String, Int>()
    private val json = Json { ignoreUnknownKeys = true }

    fun init() {
        @Serializable
        data class MyData(@SerialName("DOI") val doi: String = "", @SerialName("val") val value: Int = 0)

        val f = File("/home/ralf/IdeaProjects/LitBall-training/pred.json")
        if (f.exists()) {
            val lines = f.readLines()
            val maps = lines.map {
                json.decodeFromString<MyData>(it)
            }
            maps.forEach {
                map[it.doi] = it.value
            }
        } else
            throw Exception("File to open: ${CurrentPaperList.fileName} does not exist")

        initialized = true
    }

    fun enrich(doi: String?): Int? {
        return if (initialized)
            map[doi]
        else
            null
    }
}