package drazek.jiyt.ui.components

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.view.inputmethod.InputMethodManager
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
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

    val context = LocalContext.current
    val view = LocalView.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter file name") },
        text = {
            OutlinedTextField(
                value = textFieldContent,
                onValueChange = { updatedValue: TextFieldValue -> textFieldContent = updatedValue },
                label = { Text("Animation title") },
                singleLine = true,
                suffix = { Text(".json") },
                modifier = Modifier
                    .fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)

                onConfirm("${textFieldContent.text}.json")
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