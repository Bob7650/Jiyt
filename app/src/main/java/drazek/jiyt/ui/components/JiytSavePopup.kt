package drazek.jiyt.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import drazek.jiyt.ui.theme.JiytTheme

@Composable
fun JiytSavePopup(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var textFieldContent by rememberSaveable(stateSaver = TextFieldValue. Saver) {
        mutableStateOf(TextFieldValue("", TextRange(0, 7)))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter file name") },
        text = {
            OutlinedTextField(
                value = textFieldContent,
                onValueChange = { updatedValue: TextFieldValue -> textFieldContent = updatedValue },
                label = { Text("Animation title") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(textFieldContent.text)
                onDismiss()
            }) { Text("Save") }
        },
        modifier = modifier
    )
}

@Preview
@Composable
private fun PrevPopup() {
    JiytTheme {
        JiytSavePopup(
            onDismiss = {},
            onConfirm = {}
        )
    }
}