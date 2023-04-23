package org.reactome.lit_ball_tagger.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

internal class RootStore {
    var state: RootState by mutableStateOf(initialState())
        private set

    fun setFromDb(map: MutableMap<String,SerialDBClass>) {
        val settings = map["settings"]
        if (settings is Settings) {
            setState { copy(settings = settings) }
        }
        SerialDB.commit()
    }

    val onRailItemClicked: List<() -> Unit> = listOf(
        ::buttonInfo, ::buttonNew, ::buttonImport, ::buttonExport, ::buttonSave, ::buttonSettings, ::buttonExit)
    private fun buttonInfo() {
    }
    private fun buttonNew() {
        CurrentTitleList.new()
    }
    private fun buttonImport() {
        CurrentTitleList.import()
    }
    private fun buttonExport() {
        CurrentTitleList.export()
    }
    private fun buttonSave() {
        CurrentTitleList.save()
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

    fun onEditorCloseClicked() {
        setState { copy(editingItemId = null) }
    }

    fun onEditorTextChanged(text: String) {
        setState {
            updateItem(id = requireNotNull(editingItemId)) { it.copy(text = text) }
        }
    }

    fun onEditorDoneChanged(/*isDone: Boolean*/) {
        /*
        setState {
            updateItem(id = requireNotNull(editingItemId)) { it.copy(isDone = isDone ) }
        }
        */
    }

    private fun RootState.updateItem(id: Int, transformer: (Title) -> Title): RootState =
        copy(items = items.updateItem(id = id, transformer = transformer))

    private fun initialState(): RootState =
        RootState(
            items = TitleList(list =
                (1..55).map { id ->
                    Title(id = id, text = "Some text $id", tag = Tag.Exp )
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

