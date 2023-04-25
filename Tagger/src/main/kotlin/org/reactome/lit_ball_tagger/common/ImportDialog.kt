package org.reactome.lit_ball_tagger.common

import androidx.compose.runtime.Composable
import kotlinx.coroutines.Job
import java.io.File

@Suppress("FunctionName")
@Composable
internal fun ImportDialog(
    startPath: String?,
    onResult: (file: File) -> Job,
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
