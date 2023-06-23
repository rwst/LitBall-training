package org.reactome.lit_ball_tagger.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

internal class RootStore {
    var state: RootState by mutableStateOf(initialState())
        private set

    val onRailItemClicked: List<() -> Unit> = listOf(
        ::buttonInfo,
        ::buttonNew,
        ::buttonOpen,
        ::buttonImport,
        ::buttonExport,
        ::buttonSave,
        ::buttonSettings,
        ::buttonExit
    )

    private fun buttonInfo() {
        setState { copy(infoList = true) }
    }

    private fun buttonNew() {
        setState { copy(newList = true) }
    }

    private fun buttonOpen() {
        setState { copy(openList = true) }
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

    fun onTagsButtonClicked() {
        setState { copy(editTags = true) }
    }

    fun onEnrichButtonClicked() {
        setState { copy(enrichItems = true) }
    }
    fun onEnrichDone() {
        setState { copy(enrichItems = false) }
    }
    fun onEditTagsDone() {
        setState { copy(editTags = false) }
    }

    fun onItemClicked(id: Int) {
        setState { copy(editingItemId = id) }
    }

    fun onItemDeleteClicked(id: Int) {
        setState { copy(items = CurrentPaperList.toListWithItemRemoved(id)) }
    }

    fun onItemRadioButtonClicked(id: Int, btn: Int) {
        CurrentPaperList.setTag(id, btn)
    }

    fun onInfoDoneClicked() {
        setState { copy(infoList = false) }
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
        setState { copy(items = CurrentPaperList.toList(), newList = false) }
    }

    fun onOpenFileDoneChanged() {
        setState { copy(items = CurrentPaperList.toList(), openList = false) }
    }

    fun onImportDoneChanged() {
        setState { copy(items = CurrentPaperList.toList(), doImport = false) }
    }

    fun onSaveDoneChanged() {
        setState { copy(doSave = false) }
    }

    suspend fun onItemsChanged() {
        // TODO: This is a hack.
        setState { copy(items = emptyList()) }
        delay(50)
        setState { copy(items = CurrentPaperList.toList()) }
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
        val items: List<Paper> = CurrentPaperList.toList(),
        val settings: Settings = Settings,
        val activeRailItem: String = "",
        val editingItemId: Int? = null,
        val editingSettings: Boolean = false,
        val infoList: Boolean = false,
        val newList: Boolean = false,
        val openList: Boolean = false,
        val doImport: Boolean = false,
        val doSave: Boolean = false,
        val editTags: Boolean = false,
        val enrichItems: Boolean = false,
    )
}

