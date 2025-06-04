package drazek.jiyt.ui.addAnimScreen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.savedstate.savedState
import drazek.jiyt.ui.components.JiytColorPickerPopup
import drazek.jiyt.ui.components.JiytLEDMatrixEditor
import drazek.jiyt.ui.components.JiytToolBar
import drazek.jiyt.ui.components.JiytTopAppBar
import drazek.jiyt.ui.theme.JiytTheme
import drazek.jiyt.util.ToolTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val GRID_SIZE = 16

@Composable
fun JiytAddAnimationScreen(
    viewModel: JiytAddAnimScreenVM = viewModel(),
    onBackClicked: () -> Unit,
) {
    var showPicker = remember { mutableStateOf(false) }
    var selectedColor = remember { mutableStateOf(Color.White) }

    val context = LocalContext.current

    LaunchedEffect(Unit) { 
        viewModel.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    val grid = remember {
        mutableStateListOf(
            *Array(GRID_SIZE) { mutableStateListOf(*Array(GRID_SIZE) { Color.Black }) }
        )
    }

    if (showPicker.value){
        JiytColorPickerPopup(
            onColorSelected = { color -> selectedColor.value = color },
            onDismiss = { showPicker.value = false }
        )
    }

    Scaffold(
        topBar = { JiytTopAppBar(title = "Add animation",canGoBack = true, onBackClicked = onBackClicked) }
    ) { contentPadding->
        Column(
            modifier = Modifier.padding(contentPadding)
        ) {
            JiytLEDMatrixEditor(
                changePixel = { x, y -> grid[y][x] = if (viewModel.tool.value== ToolTypes.Eraser) Color.Black else selectedColor.value },
                updateGrid = { viewModel.saveGridState(grid.map { it.toList() }) },
                seePixel = { x,y -> grid[y][x] }
            )

            JiytToolBar(
                onPen = {viewModel.onPenClick()},
                onRedo = {
                    val nextGrid = viewModel.popFuture()
                    if (nextGrid != null) {
                        for (y in nextGrid.indices) {
                            for (x in nextGrid[y].indices) {
                                grid[y][x] = nextGrid[y][x]
                            }
                        }
                    }
                },
                onUndo = {
                    val previousGrid = viewModel.popHistory()
                    if(previousGrid!=null){
                        for(y in previousGrid.indices){
                            for(x in previousGrid[y].indices){
                                grid[y][x] = previousGrid[y][x]
                            }
                        }
                    }
                },
                onEraser = { viewModel.onEraserClick() },
                onColorPicker = { showPicker.value = true },
                isUndoActive = viewModel.history.size-1!=0,
                isRedoActive = viewModel.future.isNotEmpty()
            )

            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            ){
                IconButton(
                    onClick = { viewModel.onPreviousFrameClick() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous frame",
                        modifier = Modifier.fillMaxSize())
                }

                Button(
                    onClick = {
                        val data = viewModel.convertToJSON(grid.map { it.toList() })
                        viewModel.onSaveClick(context = context, fileName = "test", jsonData = data)
                    },
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White,
                        disabledContentColor = Color.Black,
                        disabledContainerColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)) {
                    Text("Save")
                }

                IconButton(
                    onClick = { viewModel.onNextFrameClick() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next frame",
                        modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL_7_PRO,
)
@Composable
private fun PrevScreen() {
    JiytTheme {
        JiytAddAnimationScreen(onBackClicked = {})
    }
}