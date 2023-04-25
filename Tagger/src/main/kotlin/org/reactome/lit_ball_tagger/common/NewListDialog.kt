package org.reactome.lit_ball_tagger.common

import androidx.compose.runtime.Composable
import kotlinx.coroutines.Job
import java.io.File
import kotlin.reflect.KFunction1

@Suppress("FunctionName")
@Composable
internal fun NewListDialog(
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
