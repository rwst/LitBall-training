package org.reactome.lit_ball_tagger.common.dialog

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.reactome.lit_ball_tagger.common.CurrentPaperList

@OptIn(ExperimentalMaterialApi::class)
@Suppress("FunctionName")
@Composable
internal fun InfoDialog(onDoneClicked: () -> Unit) {
    androidx.compose.material.AlertDialog(
        modifier = Modifier.fillMaxSize(fraction = 0.5F),
        title = {
            Text(text = "File stats")
        },
        text = {
                Text(
                    text = CurrentPaperList.stats(),
                    maxLines = 10)
        },
        onDismissRequest = {},
        confirmButton = {
            TextButton(
                onClick = { onDoneClicked() },
            ) {
                Text("Back")
            }
        },
    )
}
