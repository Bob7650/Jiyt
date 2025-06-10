package drazek.jiyt.ui.animlist

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import drazek.jiyt.ui.components.JiytBottomSheet
import drazek.jiyt.ui.components.JiytExpandableListElement
import drazek.jiyt.ui.components.JiytFloatingActionButton
import drazek.jiyt.ui.components.JiytTopAppBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JiytAnimListScreen(
    navToAnimEditor: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: JiytAnimListVM = viewModel(
        factory = JiytAnimListVMFactory(LocalContext.current)
    )
) {

    /**
     * VARIABLES
     */
    val context = LocalContext.current

    // Controlling the bottom sheet
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    // Entries for lazy column
    val entries = viewModel.storageManager.animListEntries

    // Entry that called the bottom sheet
    var selectedEntry = ""

    // Update entries every time user enters this screen
    LaunchedEffect(Unit) {
        viewModel.storageManager.updateEntriesFromStorage()
    }

    /**
     *
     * COMPOSABLE FUNCTIONS
     *
     */

    Scaffold(
        topBar = { JiytTopAppBar(title = "Animations list", canGoBack = false) },
        floatingActionButton = { JiytFloatingActionButton({navToAnimEditor("")}) }
    ) { contentPadding ->

        // LAZY COLUMN
        LazyColumn(Modifier.padding(contentPadding)) {
            items(entries) { entry ->

                JiytExpandableListElement(
                    elementTitle = entry.fileName,
                    onSettingsClick = {
                        navToAnimEditor(viewModel.storageManager.getFileDataFromName(fileName = entry.fileName))
                    },
                    onPlayAnimationClick = {},
                    onLongPress = { elementTitle ->
                        showBottomSheet = true
                        selectedEntry = elementTitle
                    }
                )
            }
        }

        // BOTTOM SHEET
        if(showBottomSheet){
            JiytBottomSheet(
                onDelete = {
                    scope.launch {
                        viewModel.storageManager.removeFileFromStorage(selectedEntry)
                        sheetState.hide()
                    }.invokeOnCompletion {
                        if(!sheetState.isVisible){
                            showBottomSheet = false
                        }
                    }

                },
                onDismiss = {
                    showBottomSheet = false
                },
                sheetState = sheetState,
            )
        }
    }

}