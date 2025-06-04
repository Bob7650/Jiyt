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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import drazek.jiyt.ui.addAnimScreen.GRID_SIZE
import drazek.jiyt.ui.theme.JiytTheme
import drazek.jiyt.util.ToolTypes

@Composable
fun JiytLEDMatrixEditor(
    modifier: Modifier = Modifier,
    changePixel: (Int, Int, Color) -> Unit,
    seePixel: (Int,Int) -> Color,
    updateGrid: () -> Unit,
    getTool: () -> ToolTypes
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        updateGrid()
                        //viewModel.saveGridState(grid.map{ it.toList() })
                    }
                ) { change, dragAmount ->
                    val offset = change.position
                    val boxWidth = size.width.toFloat()
                    val cellSize = boxWidth / GRID_SIZE


                    val x = (offset.x / cellSize).toInt().coerceIn(0, GRID_SIZE - 1)
                    val y = (offset.y / cellSize).toInt().coerceIn(0, GRID_SIZE - 1)

                    if(getTool() == ToolTypes.Pen)
                        changePixel(x,y,Color.White)
                    else if(getTool() == ToolTypes.Eraser)
                        changePixel(x,y,Color.Black)
                    //update (idk if that is nescjksehesry)
                }
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            for (y in 0 until GRID_SIZE) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (x in 0 until GRID_SIZE) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(1.dp)
                                .background(
                                    seePixel(x,y),
                                    shape = RectangleShape
                                )
                        )
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
        JiytLEDMatrixEditor(changePixel = {x,y,z ->}, seePixel = {x,y -> Color.Black}, updateGrid = {}, getTool = { ToolTypes.Pen })
    }
}