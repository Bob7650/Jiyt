package drazek.jiyt

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.gson.Gson
import drazek.jiyt.ui.JiytMainScreenVM
import drazek.jiyt.ui.animeditor.JiytAnimEditorArgs
import drazek.jiyt.ui.animeditor.JiytAnimEditorScreen
import drazek.jiyt.ui.animlist.JiytAnimListArgs
import drazek.jiyt.ui.animlist.JiytAnimListScreen
import drazek.jiyt.ui.btscreen.JiytBTSettingsArgs
import drazek.jiyt.ui.btscreen.JiytBTSettingsScreen
import drazek.jiyt.ui.data.JiytAnimListEntry
import drazek.jiyt.util.JiytBluetoothUtil
import drazek.jiyt.util.JiytStorageManager

@Composable
fun JiytMainScreen(
    viewModel: JiytMainScreenVM = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val sharedStorageManager = JiytStorageManager(LocalContext.current.applicationContext)
    val bluetoothUtil = JiytBluetoothUtil(LocalContext.current.applicationContext)

    LaunchedEffect(Unit) {
        bluetoothUtil.setup()
    }

    NavHost(
        navController = navController,
        startDestination = JiytAnimListArgs,
    ){
        // ANIMATION LIST
        composable<JiytAnimListArgs>{
            JiytAnimListScreen(
                navToAnimEditor = { jsonEntry: String ->
                    navController.navigate(JiytAnimEditorArgs(jsonEntry))
                },
                navToBTSet = {
                    navController.navigate(JiytBTSettingsArgs)
                },
                viewModel = viewModel(
                    factory = viewModel.getJiytAnimListViewVMFactory(sharedStorageManager, bluetoothUtil)
                )
            )
        }

        // EDITOR
        composable<JiytAnimEditorArgs> {
            // Get argument class
            val args = it.toRoute<JiytAnimEditorArgs>()

            // Check if passed argument represents a file, if not return null
            var entry: JiytAnimListEntry? =
            if(args.jsonData.isEmpty()){
                null
            }else{
                Gson().fromJson(args.jsonData, JiytAnimListEntry::class.java)
            }

            JiytAnimEditorScreen(
                animEntry = entry,
                navigateBack = {
                    navController.popBackStack()
                },
                viewModel = viewModel(
                    factory = viewModel.getJiytAnimEditorVMFactory(sharedStorageManager)
                )
            )
        }

        // BTSettings
        composable<JiytBTSettingsArgs> {
            JiytBTSettingsScreen(
                navToList = {
                    navController.navigate(JiytAnimListArgs)
                },
                viewModel = viewModel(
                    factory = viewModel.getJiytBTScreenVMFactory(bluetoothUtil)
                )
            )
        }
    }
}