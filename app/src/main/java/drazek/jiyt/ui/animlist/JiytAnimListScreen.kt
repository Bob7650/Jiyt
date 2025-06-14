package drazek.jiyt.ui.animlist

import androidx.compose.foundation.layout.Column
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
import drazek.jiyt.ui.components.JiytBottomSheet
import drazek.jiyt.ui.components.JiytExpandableListElement
import drazek.jiyt.ui.components.JiytFloatingActionButton
import drazek.jiyt.ui.components.JiytTopAppBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JiytAnimListScreen(
    navToAnimEditor: (String) -> Unit,
    navToBTSet: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: JiytAnimListVM
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

    LaunchedEffect(Unit) {
        // Update entries
        viewModel.storageManager.updateEntriesFromStorage()
    }



    /**
     *
     * COMPOSABLE FUNCTIONS
     *
     */

    Scaffold(
        topBar = { JiytTopAppBar(title = "Animations list", canSettings = true, onBTSettings = navToBTSet, bluetoothState = viewModel.bluetoothUtil.bluetoothState) },
        floatingActionButton = { JiytFloatingActionButton({navToAnimEditor("")}) }
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {

            // LAZY COLUMN
            LazyColumn {
                items(entries) { entry ->

                    JiytExpandableListElement(
                        entry = entry,
                        onSettingsClick = {
                            navToAnimEditor(viewModel.storageManager.getFileContentFromName(fileNameExt = "${entry.fileName}.json"))
                        },
                        onPlayAnimationClick = { animationName ->
                            viewModel.sendDataToConnectedDevice(animationName)
                        },
                        onLongPress = { elementTitle ->
                            showBottomSheet = true
                            selectedEntry = elementTitle
                        },
                    )

                }
            }

            // BOTTOM SHEET
            if (showBottomSheet) {
                JiytBottomSheet(
                    onDelete = {
                        scope.launch {
                            viewModel.storageManager.removeFileFromStorage(selectedEntry)
                            sheetState.hide()
                        }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
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

}