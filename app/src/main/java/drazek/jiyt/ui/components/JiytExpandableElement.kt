package drazek.jiyt.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import drazek.jiyt.ui.data.JiytAnimListEntry
import drazek.jiyt.ui.theme.JiytTheme
import drazek.jiyt.util.JiytPreviewMaker

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun JiytExpandableListElement(
    entry: JiytAnimListEntry,
    onSettingsClick: () -> Unit,
    onPlayAnimationClick: (String) -> Unit,
    onLongPress: (String) -> Unit,
    defaultState: Boolean = false) {
    var expanded by remember { mutableStateOf(defaultState) }

    val haptics = LocalHapticFeedback.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(12.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(13.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = {
                            expanded = !expanded
                        },
                        onLongClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            // display bottom sheet
                            onLongPress(entry.fileName)
                        },
                        onLongClickLabel = "Animation Options"
                    )
            ){
                Text(
                    text = entry.fileName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                    contentDescription = null
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(animationSpec = tween(300)),
                exit = shrinkVertically(animationSpec = tween(300))
            ) {
                Column(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)){

                    // PREVIEW
                    Image(
                        bitmap = entry.prevImage.asImageBitmap(),
                        contentDescription = null,
                        filterQuality = FilterQuality.None,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        // PLAY ANIMATION BUTTON
                        Button(
                            modifier = Modifier.padding(end = 5.dp),
                            shape = RoundedCornerShape(5.dp),
                            onClick = {
                                onPlayAnimationClick(entry.fileName)
                            }) {
                            Text(text = "Display")
                        }

                        // SETTINGS BUTTON
                        Button(
                            modifier = Modifier.padding(start = 5.dp, end = 5.dp),
                            shape = RoundedCornerShape(5.dp),
                            onClick = onSettingsClick) {
                            Icon(imageVector = Icons.Filled.Settings, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PrevExpElement() {
    JiytTheme {
        JiytExpandableListElement(JiytAnimListEntry(fileName = "Example animation", data = List(16){List(16){Color.Red} }, prevImage = JiytPreviewMaker().getDefaultPreview()), {}, {}, {})
    }
}

@Preview
@Composable
private fun PrevExpElementExpanded() {
    JiytTheme {
        JiytExpandableListElement(JiytAnimListEntry(fileName = "Example animation", data = List(16){List(16){Color.Red} }, prevImage = JiytPreviewMaker().getDefaultPreview()), {}, {}, {}, defaultState = true)
    }
}