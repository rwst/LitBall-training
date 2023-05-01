@file:Suppress("FunctionName")

package org.reactome.lit_ball_tagger.common.dialog

import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.window.Dialog
import org.reactome.lit_ball_tagger.common.Settings

@Composable
internal fun SettingsDialog(
    setting: Settings,
    onCloseClicked: () -> Unit)
  {
    Dialog(
        title = "Edit settings",
        onCloseRequest = onCloseClicked,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(
                value = "Path to database storage",
                onValueChange = {}
            )
        }
    }
}