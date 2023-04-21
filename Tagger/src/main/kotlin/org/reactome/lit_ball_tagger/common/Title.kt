package org.reactome.lit_ball_tagger.common

import kotlinx.serialization.Serializable

@Serializable
data class TitleList (
    val list: MutableList<Title> = mutableListOf()
) : SerialDBClass() {
    fun updateItem(id: Int, transformer: (Title) -> Title) : TitleList {
        list.replaceFirst (transformer) { it.id == id }
        return this
    }
}
@Serializable
data class Title(
    val id: Int,
    val text: String = "",
    val setting: QuerySetting = QuerySetting(),
    val actions: MutableList<LitAction> = mutableListOf()
) : SerialDBClass()
{

    override fun toString(): String {
        return "Query(setting=$setting, actions=$actions)"
    }
}
