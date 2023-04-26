@file:Suppress("FunctionName")

package org.reactome.lit_ball_tagger.common

import androidx.compose.runtime.Composable
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