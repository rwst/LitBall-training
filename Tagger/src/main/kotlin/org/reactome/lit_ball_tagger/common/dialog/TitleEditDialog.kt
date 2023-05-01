@file:Suppress("FunctionName")

package org.reactome.lit_ball_tagger.common.dialog

import androidx.compose.runtime.Composable
import org.reactome.lit_ball_tagger.common.Paper
import kotlin.reflect.KFunction0
import kotlin.reflect.KFunction1

@Composable
fun TitleEditDialog(
    item: Paper,
    onCloseClicked: KFunction0<Unit>,
    onTextChanged: KFunction1<String, Unit>,
    onDoneChanged: () -> Unit
) {
    println(item)
}