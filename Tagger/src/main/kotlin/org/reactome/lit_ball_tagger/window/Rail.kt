@file:Suppress("FunctionName")

package org.reactome.lit_ball_tagger.window

import androidx.compose.material.Icon
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun Rail(
    onRailItemClicked: List<() -> Unit>,
    onExit: () -> Unit,
) {
    val selectedItem by remember { mutableStateOf(0) }
    data class RailItem(val text: String, val icon: ImageVector, val actionIndex: Int, val extraAction: (() -> Unit)? = null)

    val items = listOf(
        RailItem("Info", Icons.Filled.Info, 0),
        RailItem("New", Icons.Filled.FileOpen, 1),
        RailItem("Open", Icons.Filled.FileOpen, 2),
        RailItem("Import", Icons.Filled.Download, 3),
        RailItem("Export", Icons.Filled.Publish, 4),
        RailItem("Save", Icons.Filled.Save, 5),
        RailItem("Settings", Icons.Filled.Settings, 6),
        RailItem("Exit", Icons.Filled.ExitToApp, 7, onExit)
    )

    NavigationRail {
        items.forEach { item ->
            NavigationRailItem(
                onClick = {
                    onRailItemClicked[item.actionIndex]()
                    item.extraAction?.invoke()
                },
                icon = { Icon(item.icon, null) },
                label = { Text(item.text) },
                selected = selectedItem == item.actionIndex
            )
        }
    }
}