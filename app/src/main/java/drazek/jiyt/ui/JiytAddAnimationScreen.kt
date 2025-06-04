package drazek.jiyt.ui

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import drazek.jiyt.ui.components.JiytLEDMatrixEditor
import drazek.jiyt.ui.components.JiytToolBar
import drazek.jiyt.ui.components.JiytTopAppBar
import drazek.jiyt.ui.theme.JiytTheme

@Composable
fun JiytAddAnimationScreen() {
    Scaffold(
        topBar = { JiytTopAppBar(title = "Add animation",canGoBack = true) }
    ) { contentPadding->
        Column(
            modifier = Modifier.padding(contentPadding)
        ) {
            JiytLEDMatrixEditor()

            JiytToolBar(
                onPen = {},
                onRedo = {},
                onUndo = {},
                onEraser = {},
                onColorPicker = {}
            )

            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            ){
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous frame",
                        modifier = Modifier.fillMaxSize())
                }

                Button(
                    onClick = {},
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

                IconButton(onClick = {}) {
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
        JiytAddAnimationScreen()
    }
}