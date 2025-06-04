package drazek.jiyt.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun JiytColorPickerPopup(
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    val controller = rememberColorPickerController()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pick a color") },
        text = {
            HsvColorPicker(
                modifier = Modifier.height(300.dp),
                onColorChanged = { colorEnvelope ->
                    onColorSelected(colorEnvelope.color)
                },
                controller = controller
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Done") }
        }
    )
}

@Preview
@Composable
private fun PrevPicker() {
    JiytColorPickerPopup({},{})
}
