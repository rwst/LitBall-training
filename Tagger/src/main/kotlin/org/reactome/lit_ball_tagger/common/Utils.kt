package org.reactome.lit_ball_tagger.common

import java.io.File

@Suppress("unused")
inline fun <reified T> Any?.tryCast(block: T.() -> Unit) {
    if (this is T) {
        block()
    }
}

@Suppress("unused")
inline fun <T : Any> MutableList<T>.replaceFirst(transformer: (T) -> T, block: (T) -> Boolean): MutableList<T> {
    val i = indexOfFirst { block(it) }
    this[i] = transformer(this[i])
    return this
}

fun deleteFilesStartingWith(path: String, prefix: String) {
    val currentDirectory = File(path)
    val filesToDelete = currentDirectory.listFiles { _, name -> name.startsWith(prefix.substringAfterLast("/")) }

    filesToDelete?.forEach { file ->
        if (file.isFile) {
            file.delete()
            println("Deleted file: ${file.name}")
        }
    }
}
