package org.reactome.lit_ball_tagger.common

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object CurrentTitleList {
    var list: MutableList<Title> = mutableListOf()
    private var path: String? = null
    fun updateItem(id: Int, transformer: (Title) -> Title) : CurrentTitleList {
        list.replaceFirst (transformer) { it.id == id }
        return this
    }

    fun new(file: File): CurrentTitleList {
        path = file.absolutePath
        return this
    }
    fun save() {
        if (path == null) return
        val pathStr: String = path as String
        val text = Json.encodeToString(list)
        File(pathStr).writeText(text)
    }
    fun export() {}
    fun import() {}
}