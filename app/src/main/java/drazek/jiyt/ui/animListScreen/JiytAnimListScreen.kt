package drazek.jiyt.ui.animListScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import drazek.jiyt.ui.components.JiytExpandableListElement
import drazek.jiyt.ui.components.JiytFloatingActionButton
import drazek.jiyt.ui.components.JiytTopAppBar
import drazek.jiyt.ui.theme.JiytTheme
import drazek.jiyt.util.JiytAnimListEntry

@Composable
fun JiytAnimListScreen(
    navToAnimEditor: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: JiytAnimListVM = viewModel()
) {

    val context = LocalContext.current

    Scaffold(
        topBar = { JiytTopAppBar(title = "Animations list", canGoBack = false) },
        floatingActionButton = { JiytFloatingActionButton({navToAnimEditor("")}) }
    ) { contentPadding ->
        LazyColumn(Modifier.padding(contentPadding)) {
            items((viewModel.fetchFilesFromStorage(context)).toList(), key = { it }) { fileName ->
                JiytExpandableListElement(
                    elementTitle = fileName,
                    onSettingsClick = {
                        navToAnimEditor(viewModel.getFileContentsByName(fileName, context))
                    },
                    onPlayAnimationClick = {})
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
        JiytAnimListScreen({})
    }
}