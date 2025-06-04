package drazek.jiyt.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import drazek.jiyt.ui.components.JiytExpandableListElement
import drazek.jiyt.ui.components.JiytFloatingActionButton
import drazek.jiyt.ui.components.JiytTopAppBar
import drazek.jiyt.ui.theme.JiytTheme

@Composable
fun JiytAnimListScreen(
    onAddClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = { JiytTopAppBar(title = "Animations list", canGoBack = false) },
        floatingActionButton = { JiytFloatingActionButton(onAddClicked) }
    ) { contentPadding ->
        LazyColumn(Modifier.padding(contentPadding)) {
            items((0 until 4).toList(), key = { it }) { item ->
                JiytExpandableListElement(item = item)
            }
        }
    }

}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL,
)
@Composable
private fun PrevScreen() {
    JiytTheme {
        JiytAnimListScreen({})
    }
}