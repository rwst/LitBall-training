@file:Suppress("FunctionName")

package org.reactome.lit_ball_tagger.window

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.runBlocking
import org.reactome.lit_ball_tagger.common.CurrentPaperList
import org.reactome.lit_ball_tagger.common.EnrichedItems
import org.reactome.lit_ball_tagger.common.Paper
import org.reactome.lit_ball_tagger.common.Tag
import org.reactome.lit_ball_tagger.common.dialog.FlagBoxes
import org.reactome.lit_ball_tagger.common.dialog.RadioButtonOptions

@Suppress("FunctionName")
@Composable
internal fun MainContent(
    modifier: Modifier = Modifier,
    items: List<Paper>,
    onItemClicked: (id: Int) -> Unit,
    onItemDeleteClicked: (id: Int) -> Unit,
    onItemRadioButtonClicked: (id: Int, btn: Int) -> Unit,
    onRailItemClicked: List<() -> Unit>,
    onExit: () -> Unit,
    onTagsButtonClicked: () -> Unit,
    onEnrichButtonClicked: () -> Unit,
    onItemFlagsClicked: (Boolean) -> Unit,
) {
    Row(modifier) {
        Rail(
            onRailItemClicked = onRailItemClicked,
            onExit,
        )

        ListContent(
            items = items,
            onItemClicked = onItemClicked,
            onItemDeleteClicked = onItemDeleteClicked,
            onItemRadioButtonClicked = onItemRadioButtonClicked,
            onTagsButtonClicked = onTagsButtonClicked,
            onEnrichButtonClicked = onEnrichButtonClicked,
            onItemFlagsClicked = onItemFlagsClicked,
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ListContent(
    items: List<Paper>,
    onItemClicked: (id: Int) -> Unit,
    onItemDeleteClicked: (id: Int) -> Unit,
    onItemRadioButtonClicked: (id: Int, btn: Int) -> Unit,
    onTagsButtonClicked: () -> Unit,
    onEnrichButtonClicked: () -> Unit,
    onItemFlagsClicked: (Boolean) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val lazyListState = rememberLazyListState()

    val onKeyDownSuspend: suspend (KeyEvent) -> Boolean = {
        when (it.type) {
            KeyEventType.KeyUp -> false

            else -> {
                val topItem = lazyListState.firstVisibleItemIndex
                val topOffset = lazyListState.firstVisibleItemScrollOffset
                when (it.key) {
                    Key.DirectionUp -> {
                        if (topOffset > 0)
                            lazyListState.scrollToItem(topItem)
                        else if (topItem > 0)
                            lazyListState.scrollToItem(topItem - 1)
                        if (topOffset > 0)
                            lazyListState.scrollToItem(topItem)
                        else if (topItem > 0)
                            lazyListState.scrollToItem(topItem - 1)
                        true
                    }

                    Key.DirectionDown -> {
                        lazyListState.scrollToItem(topItem + 1)
                        true
                    }

                    else -> false
                }
            }
        }
    }
    val onKeyDown: (KeyEvent) -> Boolean = {
        runBlocking { onKeyDownSuspend(it) }
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .focusRequester(focusRequester)
            .clickable { focusRequester.requestFocus() }
            .onPreviewKeyEvent(onKeyDown)
    ) {
        var switchChecked by remember { mutableStateOf(false) }

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = {},
                    modifier = Modifier.padding(0.dp)
                ) {
                    Text(CurrentPaperList.fileName + " " + lazyListState.firstVisibleItemIndex.toString() + '/' + items.size.toString())
                }
                Spacer(modifier = Modifier.fillMaxWidth().weight(1f))
                Switch (
                    checked = switchChecked,
                    onCheckedChange = {
                        switchChecked = it
                        onItemFlagsClicked(it)
                    },
                )
                Button(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    onClick = onEnrichButtonClicked,
                ) {
                    Text("Enrich Items")
                }
                Button(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    onClick = onTagsButtonClicked,
                ) {
                    Text("Set all tags")
                }
            }
            LazyColumn(
                Modifier.fillMaxSize().padding(end = 12.dp),
                lazyListState
            ) {
                items(
                    key = { it.id },
                    items = items,
                ) { item ->
                    CardWithTextIconAndRadiobutton(
                        item = item,
                        onClicked = { onItemClicked(item.id) },
                        onDeleteClicked = { onItemDeleteClicked(item.id) },
                        onOptionSelected = { btn -> onItemRadioButtonClicked(item.id, btn) },
                        switchChecked,
                    )
                    Divider()
                }
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = lazyListState
            )
        )
    }
}

@Composable
fun CardWithTextIconAndRadiobutton(
    item: Paper,
    onClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onOptionSelected: (btn: Int) -> Unit,
    switchChecked: Boolean
) {
    val cardTitle = item.details.title
    val enrichVal = EnrichedItems.enrich(item.details.externalIds?.get("DOI"))
    val radioButtonOptions = Tag.values().map { it.name }
    Card(
        elevation = 4.dp,
        backgroundColor = if (enrichVal != null) Color.LightGray else Color.White,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                if (enrichVal != null) {
                    Text(
                        text = enrichVal.toString(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                IconButton(onClick = onDeleteClicked) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Item",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = cardTitle ?: "",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1F).align(Alignment.CenterVertically)
                    .clickable { onClicked() },
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.width(16.dp))
            if (!switchChecked) {
                RadioButtonOptions(
                    radioButtonOptions,
                    item.tag.ordinal,
                    onOptionSelected,
                )
            }
            else {
                FlagBoxes(listOf("Entry", "Repl", "Asmbly", "Mosq", "V-H", "H-V", "RNA", "PTM")) {}
            }
        }
    }
}

