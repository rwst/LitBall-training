package org.reactome.lit_ball_tagger.common.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.reactome.lit_ball_tagger.common.Tag

@OptIn(ExperimentalMaterialApi::class)
@Suppress("FunctionName")
@Composable
internal fun EditTagsDialog(
    onResult: (tag: Tag) -> Unit,
    onDoneChanged: () -> Unit,
) {
    val radioButtonOptions = Tag.values().map { it.name }
    val selected = 1
    var selectedOptionIndex by remember { mutableStateOf(selected) }
    androidx.compose.material.AlertDialog(
        onDismissRequest = {
            onDoneChanged()
        },
        title = {
            Text(text = "Set all tags")
        },
        text = {
            Spacer(modifier = Modifier.height(24.dp))
            Column {
                radioButtonOptions.forEachIndexed { index, option ->
                    Row(
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            modifier = Modifier.size(14.dp),
                            selected = index == selectedOptionIndex,
                            onClick = {
                                selectedOptionIndex = index
                            }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = option)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onResult(Tag.values()[selectedOptionIndex])
                    onDoneChanged()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDoneChanged()
                }
            ) {
                Text("Dismiss")
            }
        },
    )
}