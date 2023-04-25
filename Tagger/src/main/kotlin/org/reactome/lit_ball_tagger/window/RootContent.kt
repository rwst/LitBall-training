@file:Suppress("FunctionName")

package org.reactome.lit_ball_tagger.window

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.reactome.lit_ball_tagger.common.*
import org.reactome.lit_ball_tagger.common.RootStore
import org.reactome.lit_ball_tagger.common.SettingsDialog

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
        onRailItemClicked = model.onRailItemClicked
    )

    scope.launch (Dispatchers.IO) {
        Settings.load()
    }

    state.editingItemId?.also { item ->
        TitleEditDialog(
            item = state.items.list[item],
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
                    state.items.new(it)
                } },
            onDoneChanged = model::onNewFileDoneChanged,
        )
    }
    if (state.doImport) {
        ImportDialog(
            state.settings.map["import-path"],
            onResult = {
                scope.launch (Dispatchers.IO) {
                    state.items.import(it)
                } },
            onDoneChanged = model::onImportDoneChanged,
        )
    }
}
