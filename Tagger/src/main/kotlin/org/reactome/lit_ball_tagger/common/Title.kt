package org.reactome.lit_ball_tagger.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
