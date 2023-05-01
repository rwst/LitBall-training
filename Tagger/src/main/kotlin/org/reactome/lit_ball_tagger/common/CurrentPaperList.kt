package org.reactome.lit_ball_tagger.common

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File

object CurrentPaperList {
    private var list: MutableList<Paper> = mutableListOf()
    private var path: String? = null
    private var shadowMap: MutableMap<Int, Int> = mutableMapOf()
    fun toList(): List<Paper> { return list.toList() }
    private fun updateShadowMap() {
        list.forEachIndexed { index, paper -> shadowMap[paper.id] = index }
    }
    private fun updateItem(id: Int, transformer: (Paper) -> Paper) : CurrentPaperList {
        val index = shadowMap[id] ?: return this
        list[index] = transformer(list[index])
        return this
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun new(file: File): CurrentPaperList {
        val p: String
        if (file.isDirectory) {
            Settings.map["list-path"] = file.absolutePath
            p = file.absolutePath + "/Untitled"
        }
        else {
            Settings.map["list-path"] = file.absolutePath.substringBeforeLast('/')
            p = file.absolutePath
        }
        path = p
        Settings.save()
        val f = File(p)
        if (f.exists())
            list = Json.decodeFromStream<MutableList<Paper>>(File(p).inputStream())
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
    suspend fun import(file: File): CurrentPaperList {
        if (file.isDirectory) {
            Settings.map["import-path"] = file.absolutePath
            Settings.save()
            return this
        } else {
            Settings.map["import-path"] = file.absolutePath.substringBeforeLast('/')
            Settings.save()
        }
        val lines = file.readLines()
        val maxId: Int = list.maxOfOrNull { it.id } ?: 0
        S2client.getDataFor(lines)?.mapIndexed { index, paperDetails ->
            list.add(Paper(maxId + index + 1, paperDetails, Tag.Exp))
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