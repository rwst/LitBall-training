package org.reactome.lit_ball_tagger.common

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TitleList(@Contextual val list: MutableList<Title> = mutableListOf()) {
    fun updateItem(id: Int, transformer: (Title) -> Title) : TitleList {
        list.replaceFirst (transformer) { it.id == id }
        return this
    }
}
enum class Tag {
    @SerialName("OTHER") Other,
    @SerialName("EXP") Exp,
    @SerialName("DRUG") Drug,
}
@Serializable
class Title(val id: Int, val text: String, val tag: Tag)
{

    override fun toString(): String {
        return "Title(text=$text, tag=$tag)"
    }
    fun copy(id: Int = this.id, text: String = this.text, tag: Tag = this.tag): Title { return Title(id, text, tag)
    }
}
