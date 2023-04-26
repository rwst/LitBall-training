package org.reactome.lit_ball_tagger.common

import androidx.compose.runtime.Composable
import kotlinx.coroutines.Job
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileSystemView

@Suppress("FunctionName")
@Composable
fun FileChooserDialog(
    title: String,
    path: String?,
    onResult: (file: File) -> Job,
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
    }
    onDoneChanged()
}
