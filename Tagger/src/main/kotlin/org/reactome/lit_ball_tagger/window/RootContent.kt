@file:Suppress("FunctionName")

package org.reactome.lit_ball_tagger.window

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.reactome.lit_ball_tagger.common.*
import org.reactome.lit_ball_tagger.common.RootStore
import org.reactome.lit_ball_tagger.common.SettingsDialog

@Composable
fun RootContent(modifier: Modifier = Modifier) {
    val model = remember { RootStore() }
    val state = model.state

    MainContent(
        modifier = modifier,
        items = state.items,
        onItemClicked = model::onItemClicked,
        onItemDeleteClicked = model::onItemDeleteClicked,
        onRailItemClicked = model.onRailItemClicked
    )

    LaunchedEffect(Unit) {
        SerialDB.open()
        model.setFromDb(SerialDB.get())
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
            state.items,
            onResult = model::onNewFileResult,
            onDoneChanged = model::onNewFileDoneChanged,
        )
    }
}
