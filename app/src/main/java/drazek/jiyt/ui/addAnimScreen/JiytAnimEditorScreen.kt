package drazek.jiyt.ui.addAnimScreen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import drazek.jiyt.ui.components.JiytColorPickerPopup
import drazek.jiyt.ui.components.JiytLEDMatrixEditor
import drazek.jiyt.ui.components.JiytSavePopup
import drazek.jiyt.ui.components.JiytToolBar
import drazek.jiyt.ui.components.JiytTopAppBar
import drazek.jiyt.ui.theme.JiytTheme
import drazek.jiyt.util.JiytAnimListEntry
import drazek.jiyt.util.ToolTypes

const val GRID_SIZE = 16

/* TODO: Rename to Edit animation screen, require JiytAnimListEntry
 *
 */

@Composable
fun JiytAnimEditorScreen(
    navigateBack: () -> Unit,
    animEntry: JiytAnimListEntry? = null,
    viewModel: JiytAnimEditorVM = viewModel()
) {
    /*
     *
     */

    // Basic variables
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val remFileName = remember { mutableStateOf(animEntry?.fileName ?: "New Animation.json") }

    val grid = remember {
        if (animEntry==null)
            mutableStateListOf(*Array(GRID_SIZE) { mutableStateListOf(*Array(GRID_SIZE) { Color.Black }) } )
        else
            animEntry.data.map { row ->
                    row.map { colorInt->
                        Color(green = colorInt[0]/255f, red = colorInt[1]/255f, blue = colorInt[2]/255f, alpha = 1f)
                    }.toMutableStateList()
            }.toMutableStateList()
    }


    // Signal receiver
    LaunchedEffect(Unit) { 
        viewModel.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    /*
     *
     */

    // Color Picker Popup
    var showPicker = remember { mutableStateOf(false) }
    var selectedColor = remember { mutableStateOf(Color.White) }

    if (showPicker.value){
        JiytColorPickerPopup(
            onColorSelected = { color -> selectedColor.value = color },
            onDismiss = { showPicker.value = false }
        )
    }

    // Save popup
    var showSavePopup = remember { mutableStateOf(false) }

    if(showSavePopup.value){
        JiytSavePopup(
            modifier = Modifier,
            onDismiss = {
                showSavePopup.value = false
            },
            onConfirm = { inValue ->
                if(viewModel.nameAvailable(name = inValue,context = context)) {

                    // Change name displayed by top bar
                    remFileName.value = inValue

                    // Save to file
                    viewModel.saveDataToFile(
                        context = context,
                        fileName = remFileName.value,
                        data = grid.map { it.toList() })
                }else{
                    viewModel.sendToastSignal("The name is already in use")
                }
            }
        )
    }

    /*
     *
     */

    Scaffold(
        topBar = { JiytTopAppBar(title = remFileName.value,canGoBack = true, onBackClicked = navigateBack) }
    ) { contentPadding->
        Column(
            modifier = Modifier.padding(contentPadding)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ){
                    focusManager.clearFocus()
                }
        ) {

            // LED MATRIX
            JiytLEDMatrixEditor(
                changePixel = { x, y -> grid[y][x] = if (viewModel.tool.value== ToolTypes.Eraser) Color.Black else selectedColor.value },
                updateGrid = { viewModel.saveGridState(grid.map { it.toList() }) },
                seePixel = { x,y -> grid[y][x] }
            )

            // TOOLBAR
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

            // BOTTOM BUTTONS
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            ){
                // LEFT
                IconButton(
                    onClick = { viewModel.onPreviousFrameClick() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous frame",
                        modifier = Modifier.fillMaxSize())
                }

                // SAVE
                Button(
                    onClick = {
                        // Check if there is a file associated
                        if(animEntry==null) {
                            // Ask for file name (display popup)
                            showSavePopup.value = true
                        }else{
                            viewModel.saveDataToFile(context = context, fileName = remFileName.value, data = grid.map { it.toList() })
                        }
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

                // RIGHT
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
        JiytAnimEditorScreen(
            navigateBack = {})
    }
}
