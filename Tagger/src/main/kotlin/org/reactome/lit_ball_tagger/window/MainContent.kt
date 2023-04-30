@file:Suppress("FunctionName")

package org.reactome.lit_ball_tagger.window

import androidx.compose.foundation.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.runBlocking
import org.reactome.lit_ball_tagger.common.Paper
import org.reactome.lit_ball_tagger.common.Tag

@Suppress("FunctionName")
@Composable
internal fun MainContent(
    modifier: Modifier = Modifier,
    items: List<Paper>,
    onItemClicked: (id: Int) -> Unit,
    onItemDeleteClicked: (id: Int) -> Unit,
    onRailItemClicked: List<() -> Unit>,
) {
    Row(modifier) {
        Rail(
            onRailItemClicked = onRailItemClicked,
        )

        ListContent(
            items = items,
            onItemClicked = onItemClicked,
            onItemDeleteClicked = onItemDeleteClicked
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ListContent(
    items: List<Paper>,
    onItemClicked: (id: Int) -> Unit,
    onItemDeleteClicked: (id: Int) -> Unit,
    ) {
    val focusRequester = remember { FocusRequester() }
    val lazyListState = rememberLazyListState()

    val onKeyDownSuspend: suspend (KeyEvent) -> Boolean = {
        println(it.key)
        when (it.type) {
            KeyEventType.KeyUp -> {
                false
            }
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
        LazyColumn(
            Modifier.fillMaxSize().padding(end = 12.dp),
            lazyListState
        ) {
            items(items) { item ->
                CardWithTextIconAndRadiobutton(
                    item = item,
                    onClicked = { onItemClicked(item.id) },
                    onDeleteClicked = { onItemDeleteClicked(item.id) },
                    onOptionSelected = {},
                )
                Divider()
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
    onOptionSelected: (Int) -> Unit,
    onDeleteClicked: () -> Unit,
) {
    val cardTitle = item.details.title
    val radioButtonOptions = Tag.values().map { it.name }
    Card(
        elevation = 4.dp,
        backgroundColor = Color.White,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            IconButton(onClick = onDeleteClicked) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Item",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = cardTitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1F).align(Alignment.CenterVertically),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(16.dp))
            RadioButtonOptions(
                radioButtonOptions,
                item.tag.ordinal,
                onOptionSelected
            )
        }
    }
}

@Composable
fun RadioButtonOptions(
    options: List<String>,
    defaultSelectedOptionIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    var selectedOptionIndex by remember { mutableStateOf(defaultSelectedOptionIndex) }
    options.forEachIndexed { index, option ->
        Row(
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                modifier = Modifier.size(8.dp),
                selected = index == selectedOptionIndex,
                onClick = {
                    selectedOptionIndex = index
                    onOptionSelected(selectedOptionIndex)
                }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = option,
                fontSize = 8.sp,
                color = Color.Black
            )
        }
    }
}

