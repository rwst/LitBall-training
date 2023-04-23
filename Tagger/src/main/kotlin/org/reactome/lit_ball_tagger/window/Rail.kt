@file:Suppress("FunctionName")

package org.reactome.lit_ball_tagger.window

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*

@Composable
fun Rail(
    onRailItemClicked: List<() -> Unit>,
) {
    val selectedItem by remember { mutableStateOf(0) }
// TODO
//    val items = listOf("Info", "Settings", "Exit")
//    val icons = listOf(Icons.Filled.Settings, Icons.Filled.ExitToApp)
//    val actions = listOf( {  } , { App.buttonSettings() }, { App.buttonExit() } )
    NavigationRail {
        NavigationRailItem(
            onClick = onRailItemClicked[0],
            icon = { Icon(Icons.Filled.Info, null) },
            label = { Text("Info") },
            selected = selectedItem == 0
        )
        NavigationRailItem(
            onClick = onRailItemClicked[1],
            icon = { Icon(Icons.Filled.FileOpen, null) },
            label = { Text("Open/New") },
            selected = selectedItem == 1
        )
        NavigationRailItem(
            onClick = onRailItemClicked[2],
            icon = { Icon(Icons.Filled.Download, null) },
            label = { Text("Import") },
            selected = selectedItem == 2
        )
        NavigationRailItem(
            onClick = onRailItemClicked[3],
            icon = { Icon(Icons.Filled.Publish, null) },
            label = { Text("Export") },
            selected = selectedItem == 3
        )
        NavigationRailItem(
            onClick = onRailItemClicked[4],
            icon = { Icon(Icons.Filled.Save, null) },
            label = { Text("Save") },
            selected = selectedItem == 4
        )
        NavigationRailItem(
            onClick = onRailItemClicked[5],
            icon = { Icon(Icons.Filled.Settings, null) },
            label = { Text("Settings") },
            selected = selectedItem == 5
        )
        NavigationRailItem(
            onClick = onRailItemClicked[6],
            icon = { Icon(Icons.Filled.ExitToApp, null) },
            label = { Text("Exit") },
            selected = selectedItem == 6
        )
    }
}
