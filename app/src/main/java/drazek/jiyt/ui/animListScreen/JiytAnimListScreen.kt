package drazek.jiyt.ui.animListScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import drazek.jiyt.ui.components.JiytExpandableListElement
import drazek.jiyt.ui.components.JiytFloatingActionButton
import drazek.jiyt.ui.components.JiytTopAppBar
import drazek.jiyt.ui.theme.JiytTheme

@Composable
fun JiytAnimListScreen(
    navToAnimEditor: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: JiytAnimListVM = viewModel(
        factory = JiytAnimListVMFactory(LocalContext.current)
    )
) {

    val context = LocalContext.current

    // Every time this screen is loaded update viewModel.animListEntries
    LaunchedEffect(Unit) {
        viewModel.updateEntriesFromStorage(context)
    }

    Scaffold(
        topBar = { JiytTopAppBar(title = "Animations list", canGoBack = false) },
        floatingActionButton = { JiytFloatingActionButton({navToAnimEditor("")}) }
    ) { contentPadding ->
        LazyColumn(Modifier.padding(contentPadding)) {
            items(viewModel.animListEntries) { entry ->

                JiytExpandableListElement(
                    elementTitle = entry.fileName,
                    onSettingsClick = {
                        navToAnimEditor(viewModel.getFileContentsByName(fileName = entry.fileName,context = context))
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