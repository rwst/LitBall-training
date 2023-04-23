package org.reactome.lit_ball_tagger.common

import java.io.File

object CurrentTitleList {
    var list: MutableList<Title> = mutableListOf()
    var path: String? = null
    fun updateItem(id: Int, transformer: (Title) -> Title) : CurrentTitleList {
        list.replaceFirst (transformer) { it.id == id }
        return this
    }

    fun new(file: File) {
   }
    fun save() {}
    fun export() {}
    fun import() {}
}