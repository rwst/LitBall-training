@file:Suppress("FunctionName")

package org.reactome.lit_ball_tagger.window

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.reactome.lit_ball_tagger.common.*
import org.reactome.lit_ball_tagger.common.RootStore
import org.reactome.lit_ball_tagger.common.dialog.SettingsDialog
import org.reactome.lit_ball_tagger.common.dialog.ImportDialog
import org.reactome.lit_ball_tagger.common.dialog.NewListDialog
import org.reactome.lit_ball_tagger.common.dialog.TitleEditDialog

@Composable
fun RootContent(modifier: Modifier = Modifier) {
    val model = remember { RootStore() }
    val state = model.state
    val scope = rememberCoroutineScope()

    MainContent(
        modifier = modifier,
        items = state.items,
        onItemClicked = model::onItemClicked,
        onItemDeleteClicked = model::onItemDeleteClicked,
        onItemRadioButtonClicked = model::onItemRadioButtonClicked,
        onRailItemClicked = model.onRailItemClicked
    )

    scope.launch (Dispatchers.IO) {
        Settings.load()
    }

    state.editingItemId?.also { item ->
        TitleEditDialog(
            item = state.items[item],
            onCloseClicked = model::onEditorCloseClicked,
            onTextChanged = model::onEditorTextChanged,
            onDoneChanged = model::onEditorDoneChanged,
        )
    }

    if (state.editingSettings) {
        SettingsDialog(
            state.settings,
            onCloseClicked = {})
    }
    if (state.newList) {
        NewListDialog(
            state.settings.map["list-path"],
            onResult = {
                scope.launch (Dispatchers.IO) {
                    CurrentPaperList.new(it)
                    (model::onItemsChanged)()
                }
            },
            onDoneChanged = model::onNewFileDoneChanged,
        )
    }
    if (state.doImport) {
        ImportDialog(
            state.settings.map["import-path"],
            onResult = {
                scope.launch (Dispatchers.IO) {
                    CurrentPaperList.import(it)
                    (model::onItemsChanged)()
                } },
            onDoneChanged = model::onImportDoneChanged,
        )
    }
    if (state.doSave) {
        scope.launch (Dispatchers.IO) {
            (model::onSaveDoneChanged)()
            CurrentPaperList.save()
        }
    }
}
