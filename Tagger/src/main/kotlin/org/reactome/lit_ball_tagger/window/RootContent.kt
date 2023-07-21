@file:Suppress("FunctionName")

package org.reactome.lit_ball_tagger.window

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.reactome.lit_ball_tagger.common.CurrentPaperList
import org.reactome.lit_ball_tagger.common.EnrichedItems
import org.reactome.lit_ball_tagger.common.RootStore
import org.reactome.lit_ball_tagger.common.Settings
import org.reactome.lit_ball_tagger.common.dialog.*

@Composable
fun RootContent(
    modifier: Modifier = Modifier,
    onExit: () -> Unit,
) {
    val model = remember { RootStore() }
    val state = model.state
    val scope = rememberCoroutineScope()

    MainContent(
        modifier = modifier,
        items = state.items,
        onItemClicked = model::onItemClicked,
        onItemDeleteClicked = model::onItemDeleteClicked,
        onItemRadioButtonClicked = model::onItemRadioButtonClicked,
        onRailItemClicked = model.onRailItemClicked,
        onExit,
        onTagsButtonClicked = model::onTagsButtonClicked,
        onEnrichButtonClicked = model::onEnrichButtonClicked,
        onItemFlagsClicked = model::onItemFlagsClicked,
        onFlagSet = model::onFlagSet,
    )

    scope.launch(Dispatchers.IO) {
        Settings.load()
    }

    state.editingItemId?.also { item ->
        ItemClickedDialog(
            item,
            model::onEditorCloseClicked
        )
    }

    if (state.editingSettings) {
        SettingsDialog(
            state.settings,
            onCloseClicked = {})
    }
    if (state.infoList) {
        InfoDialog(model::onInfoDoneClicked)
    }
    if (state.newList) {
        NewListDialog(
            state.settings.map["list-path"] as String,
            onResult = {
                scope.launch(Dispatchers.IO) {
                    CurrentPaperList.new(it)
                    (model::onItemsChanged)()
                }
            },
            onDoneChanged = model::onNewFileDoneChanged,
        )
    }
    if (state.openList) {
        NewListDialog(
            state.settings.map["list-path"] as String,
            onResult = {
                scope.launch(Dispatchers.IO) {
                    CurrentPaperList.open(it)
                    (model::onItemsChanged)()
                }
            },
            onDoneChanged = model::onOpenFileDoneChanged,
        )
    }
    if (state.doImport) {
        ImportDialog(
            state.settings.map["import-path"] as String,
            onResult = {
                scope.launch(Dispatchers.IO) {
                    CurrentPaperList.import(it)
                    (model::onItemsChanged)()
                }
            },
            onDoneChanged = model::onImportDoneChanged,
        )
    }
    if (state.doSave) {
        scope.launch(Dispatchers.IO) {
            (model::onSaveDoneChanged)()
            CurrentPaperList.save()
        }
    }
    if (state.editTags) {
        EditTagsDialog(
            onResult = {
                scope.launch(Dispatchers.IO) {
                    println(CurrentPaperList.toList().hashCode())
                    CurrentPaperList.setAllTags(it)
                    println(CurrentPaperList.toList().hashCode())
                    (model::onItemsChanged)()
                }
            },
            onDoneChanged = model::onEditTagsDone,
        )
    }
    if (state.enrichItems) {
        scope.launch(Dispatchers.IO) {
            if (!EnrichedItems.initialized)
                EnrichedItems.initialize()
            (model::onItemsChanged)()
            (model::onEnrichDone)()
        }
    }
}
