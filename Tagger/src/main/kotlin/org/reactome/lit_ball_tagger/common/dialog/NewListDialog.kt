package org.reactome.lit_ball_tagger.common.dialog

import androidx.compose.runtime.Composable
import kotlinx.coroutines.Job
import java.io.File

@Suppress("FunctionName")
@Composable
internal fun NewListDialog(
    startPath: String?,
    onResult: (file: List<File>) -> Job,
    onDoneChanged: () -> Unit,
)
{
    FileChooserDialog(
        "Choose a file",
        startPath,
        onResult,
        onDoneChanged,
        )
}
