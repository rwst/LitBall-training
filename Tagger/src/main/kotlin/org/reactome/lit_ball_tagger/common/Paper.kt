package org.reactome.lit_ball_tagger.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class Tag {
    @SerialName("OTHER") Other,
    @SerialName("EXP") Exp,
    @SerialName("DRUG") Drug,
}
@Serializable
class Paper(val id: Int, val details: S2Service.PaperDetails, var tag: Tag)
{

    override fun toString(): String {
        return "Paper(details=$details, tag=$tag)"
    }
}
