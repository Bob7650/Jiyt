package drazek.jiyt.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import drazek.jiyt.ui.theme.JiytTheme


@Composable
fun JiytFloatingActionButton(
    onAddClicked: () -> Unit
) {
    FloatingActionButton(onClick = onAddClicked){
        Icon(Icons.Filled.Add, contentDescription = null)
    }
}

@Preview
@Composable
private fun PrevFAB() {
    JiytTheme {
        JiytFloatingActionButton(onAddClicked = {})
    }
}