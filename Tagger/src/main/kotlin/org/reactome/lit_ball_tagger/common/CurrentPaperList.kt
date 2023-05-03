package org.reactome.lit_ball_tagger.common

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.util.*
import kotlin.math.min

object CurrentPaperList {
    private var list: MutableList<Paper> = mutableListOf()
    private var path: String? = null
    var fileName: String = ""
    private var shadowMap: MutableMap<Int, Int> = mutableMapOf()
    fun toList(): List<Paper> { return list.toList() }
    fun toListWithItemRemoved(id: Int): List<Paper> {
        list = list.filterNot { it.id == id }.toMutableList()
        return list.toList()
    }
    private fun updateShadowMap() {
        list.forEachIndexed { index, paper -> shadowMap[paper.id] = index }
    }
    private fun updateItem(id: Int, transformer: (Paper) -> Paper) : CurrentPaperList {
        val index = shadowMap[id] ?: return this
        list[index] = transformer(list[index])
        return this
    }
    @Suppress("SENSELESS_COMPARISON")
    private fun sanitize() {
        list.forEachIndexed { index, paper ->
            val newPaper: Paper = paper
            var isChanged = false
            val extIds = paper.details.externalIds?.toMutableMap()
            if (extIds != null) {
                for (entry in extIds.entries) {
                    if (entry.value == null) {
                        extIds.remove(entry.key)
                        newPaper.details.externalIds = extIds
                        isChanged = true
                    }
                }
            }
            val tldr = paper.details.tldr?.toMutableMap()
            if (tldr != null) {
                for (entry in tldr.entries) {
                    if (entry.value == null) {
                        tldr.remove(entry.key)
                        newPaper.details.tldr = tldr
                        isChanged = true
                    }
                }
            }
            if (isChanged)
                list[index] = newPaper
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun new(files: List<File>): CurrentPaperList {
        for (file in files) {
            val p: String
            if (file.isDirectory) {
                Settings.map["list-path"] = file.absolutePath
                fileName = "/Untitled"
                p = file.absolutePath + fileName
            } else {
                Settings.map["list-path"] = file.absolutePath.substringBeforeLast('/')
                p = file.absolutePath
                fileName = file.name
            }
            path = p
            Settings.save()
            val f = File(p)
            if (f.exists()) {
                if (list.isEmpty())
                    list = Json.decodeFromStream<MutableList<Paper>>(File(p).inputStream())
                else
                    list.addAll(Json.decodeFromStream<MutableList<Paper>>(File(p).inputStream()))
            }
            else
                list = mutableListOf()
        }
        updateShadowMap()
        return this
    }
    fun save() {
        if (path == null) return
        val pathStr: String = path as String
        val text = Json.encodeToString(list)
        File(pathStr).writeText(text)
    }
    fun export() {
        if (path == null) return
        val pathStr: String = path as String
        for (tag in Tag.values()) {
            val tagged = list.filter { it.tag == tag }
            val text = Json.encodeToString(tagged)
            File(pathStr + '-' + tag.name).writeText(text)
        }
    }
    suspend fun import(files: List<File>): CurrentPaperList {
        for (file in files) {
            if (file.isDirectory) {
                Settings.map["import-path"] = file.absolutePath
                Settings.save()
                return this
            } else {
                Settings.map["import-path"] = file.absolutePath.substringBeforeLast('/')
                Settings.save()
            }
            val lines = file
                .readLines()
                .filter { it.isNotBlank() }
                .map { it.uppercase(Locale.ENGLISH)
                    .removeSuffix("^M")
                    .trimEnd() }
                .toSet()
                .toList()
            val doisRequested = lines.toMutableSet()
            val chunkSize = 450
            for (n in 1..(lines.size + chunkSize - 1)/chunkSize) {
                val maxId: Int = list.maxOfOrNull { it.id } ?: 0
                val upper = min(n * chunkSize - 1, lines.size - 1)
                val lower = (n-1) * chunkSize
                S2client.getDataFor(lines.subList(lower, upper + 1))?.mapIndexed { index, paperDetails ->
                    if (paperDetails != null) {
                        list.add(Paper(maxId + index + 1, paperDetails, Tag.Exp))
                        doisRequested.remove(paperDetails.externalIds?.get("DOI").toString().uppercase(Locale.ENGLISH))
                    }
                }
            }
            sanitize()
            if (doisRequested.isNotEmpty())
                File(file.absolutePath + "-DOIs-not-found").writeText(doisRequested.toString())
        }
        updateShadowMap()
        return this
    }
    fun setTag(id: Int, btn: Int) {
        val newTag = Tag.values()[btn]
        updateItem(id
        ) {
            it.tag = newTag
            return@updateItem it
        }
    }
}