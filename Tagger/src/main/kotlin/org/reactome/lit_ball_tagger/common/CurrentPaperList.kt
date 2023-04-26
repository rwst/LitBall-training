package org.reactome.lit_ball_tagger.common

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File

object CurrentPaperList {
    var list: MutableList<Paper> = mutableListOf()
    private var path: String? = null
    fun updateItem(id: Int, transformer: (Paper) -> Paper) : CurrentPaperList {
        list.replaceFirst (transformer) { it.id == id }
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
        list = Json.decodeFromStream<MutableList<Paper>>(File(p).inputStream())
        return this
    }
    fun save() {
        if (path == null) return
        val pathStr: String = path as String
        val text = Json.encodeToString(list)
        File(pathStr).writeText(text)
    }
    fun export() {}
    suspend fun import(file: File): CurrentPaperList {
        if (file.isDirectory) {
            Settings.map["import-path"] = file.absolutePath
            Settings.save()
            return this
        }
        else {
            Settings.map["import-path"] = file.absolutePath.substringBeforeLast('/')
            Settings.save()
        }
        val lines = file.readLines()
        val pd = S2client.getDataFor(lines[0])
        val maxId: Int = list.maxOfOrNull { it.id }?: 0
        if (pd != null)
            list.add(Paper(maxId + 1, pd, Tag.Exp) )
        return this
    }
}