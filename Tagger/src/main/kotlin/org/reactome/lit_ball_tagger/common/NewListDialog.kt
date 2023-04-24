package org.reactome.lit_ball_tagger.common

import androidx.compose.runtime.Composable
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileSystemView
import kotlin.reflect.KFunction1

@Suppress("FunctionName")
@Composable
internal fun NewListDialog(
    startPath: String?,
    onResult: KFunction1<File, Unit>,
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
@Suppress("FunctionName")
@Composable
fun FileChooserDialog(
    title: String,
    path: String?,
    onResult: (File) -> Unit,
    onDoneChanged: () -> Unit,
) {
    val fileChooser = JFileChooser(FileSystemView.getFileSystemView())
    fileChooser.currentDirectory = File(path?:System.getProperty("user.dir"))
    fileChooser.dialogTitle = title
    fileChooser.fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
    fileChooser.isAcceptAllFileFilterUsed = true
    fileChooser.selectedFile = null
    if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        val file = fileChooser.selectedFile
        onResult(file)
    } else {
        onDoneChanged()
    }
}
