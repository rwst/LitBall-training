package org.reactome.lit_ball_tagger.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * The EnrichedItems class provides functionality to initialize and return a map of DOI to value.
 * It uses a JSON file to load data and update the map.
 */
object EnrichedItems {
    var initialized = false
        private set
    private val doiToValueMap = mutableMapOf<String, Int>()
    private val json = Json { ignoreUnknownKeys = true }

    fun initialize() {
        val jsonData = loadDataFromFile()
        updateDataMap(jsonData)
        initialized = true
    }
    fun enrich(doi: String?): Int? {
        return if (initialized) doiToValueMap[doi] else null
    }

    @Serializable
    data class DataItem(@SerialName("DOI") val doi: String = "", @SerialName("val") val value: Int = 0)

    private fun loadDataFromFile(): List<DataItem> {
        val inputFile = File("/home/ralf/IdeaProjects/LitBall-training/pred.json")
        require(inputFile.exists()) {
            "File to open: ${CurrentPaperList.fileName} does not exist"
        }

        val lines = inputFile.readLines()
        return lines.map { json.decodeFromString<DataItem>(it) }
    }

    private fun updateDataMap(dataList: List<DataItem>) {
        dataList.forEach {
            doiToValueMap[it.doi] = it.value
        }
    }
}