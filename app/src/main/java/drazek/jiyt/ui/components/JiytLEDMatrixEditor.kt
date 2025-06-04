package drazek.jiyt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import drazek.jiyt.ui.theme.JiytTheme

@Composable
fun JiytLEDMatrixEditor(modifier: Modifier = Modifier) {
    val gridSize = 16
    val grid = remember {
        mutableStateListOf(
            *Array(gridSize) { mutableStateListOf(*Array(gridSize) { false }) }
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = { /*add board position to the board history*/ }
                ) { change, dragAmount ->
                    val offset = change.position
                    val boxWidth = size.width.toFloat()
                    val cellSize = boxWidth / gridSize


                    val x = (offset.x / cellSize).toInt().coerceIn(0, gridSize - 1)
                    val y = (offset.y / cellSize).toInt().coerceIn(0, gridSize - 1)

                    if (!grid[y][x]) {
                        grid[y][x] = true
                        //update
                    }
                }
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            for (y in 0 until gridSize) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (x in 0 until gridSize) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(1.dp)
                                .background(
                                    if (grid[y][x]) Color.Green else Color.Black,
                                    shape = RectangleShape
                                )
                        ) {

                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PrevEditor() {
    JiytTheme {
        JiytLEDMatrixEditor()
    }
}