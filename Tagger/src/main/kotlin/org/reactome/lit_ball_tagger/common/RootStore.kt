package org.reactome.lit_ball_tagger.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

internal class RootStore {
    var state: RootState by mutableStateOf(initialState())
        private set

    val onRailItemClicked: List<() -> Unit> = listOf(
        ::buttonInfo, ::buttonNew, ::buttonImport, ::buttonExport, ::buttonSave, ::buttonSettings, ::buttonExit)
    private fun buttonInfo() {
    }
    private fun buttonNew() {
        setState { copy(newList = true) }
    }
    private fun buttonImport() {
        setState { copy(doImport = true) }
    }
    private fun buttonExport() {
        CurrentPaperList.export()
    }
    private fun buttonSave() {
        setState { copy(doSave = true) }
    }
    private fun buttonSettings() {
    }
    private fun buttonExit() {
    }
    fun onItemClicked(id: Int) {
//        setState { copy(editingItemId = id) }
    }

    fun onItemDeleteClicked(id: Int) {
//        setState { copy(items = TitleList(items.list.filterNot { it.id == id }.toMutableList())) }
    }

    fun onEditorCloseClicked() {
        setState { copy(editingItemId = null) }
    }

    fun onEditorTextChanged(text: String) {
//        setState {
//            updateItem(id = requireNotNull(editingItemId)) { it.copy(text = text) }
//        }
    }

    fun onEditorDoneChanged(/*isDone: Boolean*/) {
        /*
        setState {
            updateItem(id = requireNotNull(editingItemId)) { it.copy(isDone = isDone ) }
        }
        */
    }

    fun onNewFileDoneChanged() {
        setState { copy(items = CurrentPaperList.list, newList = false) }
    }
    fun onImportDoneChanged() {
        setState { copy(items = CurrentPaperList.list, doImport = false) }
    }
    fun onSaveDoneChanged() {
        setState { copy(doSave = false) }
    }
    fun onItemsChanged() {
        setState { copy(items = CurrentPaperList.list) }
    }
    fun onSettingsChanged(settings: Settings) {
        setState { copy(settings = settings) }
    }
//    private fun RootState.updateItem(id: Int, transformer: (Paper) -> Paper): RootState =
//        copy(items = items.updateItem(id = id, transformer = transformer))

    private fun initialState(): RootState = RootState()

    private inline fun setState(update: RootState.() -> RootState) {
        state = state.update()
    }

    data class RootState(
        val items: MutableList<Paper> = CurrentPaperList.list,
        val settings: Settings = Settings,
        val activeRailItem: String = "",
        val editingItemId: Int? = null,
        val editingSettings: Boolean = false,
        val newList: Boolean = false,
        val doImport: Boolean = false,
        val doSave: Boolean = false,
    )
}

