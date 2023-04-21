package org.reactome.lit_ball_tagger.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

internal class RootStore {
    var state: RootState by mutableStateOf(initialState())
        private set

    fun setFromDb(map: MutableMap<String,SerialDBClass>) {
        val items = map["queries"]
        if (items is TitleList) {
            setState { copy(items = items) }
        }
        val settings = map["settings"]
        if (settings is Settings) {
            setState { copy(settings = settings) }
        }
        SerialDB.commit()
    }

    val onRailItemClicked: List<() -> Unit> = listOf(
        ::buttonInfo, ::buttonNew, ::buttonImport, ::buttonExport, ::buttonSettings, ::buttonExit)
    private fun buttonInfo() {
        SerialDB.set("queries", state.items)
        SerialDB.set("settings", state.settings)
        SerialDB.commit()
    }
    private fun buttonNew() {
    }
    private fun buttonImport() {
    }
    private fun buttonExport() {
    }
    private fun buttonSettings() {
    }
    private fun buttonExit() {
    }
    fun onItemClicked(id: Int) {
        setState { copy(editingItemId = id) }
    }

    fun onItemDeleteClicked(id: Int) {
        setState { copy(items = TitleList(items.list.filterNot { it.id == id }.toMutableList())) }
    }

//    fun onNewItemClicked() {
//        setState {
//            val newItem =
//                Query(
//                    id = items.list.maxOfOrNull(Query::id)?.plus(1) ?: 1,
//                    text = "New Query"
//                )
//
//            copy(items = QueryList((items.list + newItem).toMutableList()))
//        }
//        SerialDB.commit()
//    }

    fun onEditorCloseClicked() {
        setState { copy(editingItemId = null) }
    }

    fun onEditorTextChanged(text: String) {
        setState {
            updateItem(id = requireNotNull(editingItemId)) { it.copy(text = text) }
        }
    }

    fun onEditorDoneChanged(/* isDone: Boolean */) {
        setState {
            updateItem(id = requireNotNull(editingItemId)) { it.copy(/* isDone = isDone */) }
        }
    }

    private fun RootState.updateItem(id: Int, transformer: (Title) -> Title): RootState =
        copy(items = items.updateItem(id = id, transformer = transformer))

    private fun initialState(): RootState =
        RootState(
            items = TitleList(
                (1..5).map { id ->
                    Title(id = id, text = "Some text $id")
                }.toMutableList(),
            )
        )

    private inline fun setState(update: RootState.() -> RootState) {
        state = state.update()
    }

    data class RootState(
        val items: TitleList = TitleList(),
        val settings: Settings = Settings(),
        val activeRailItem: String = "",
        val editingItemId: Int? = null,
        val editingSettings: Boolean = false,
    )
}

