package drazek.jiyt

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.gson.Gson
import drazek.jiyt.ui.JiytViewModelAnimList
import drazek.jiyt.ui.animeditor.JiytAnimEditorArgs
import drazek.jiyt.ui.animeditor.JiytAnimEditorScreen
import drazek.jiyt.ui.animlist.JiytAnimListArgs
import drazek.jiyt.ui.animlist.JiytAnimListScreen
import drazek.jiyt.ui.data.JiytAnimListEntry

@Composable
fun JiytMainScreen(
    viewModel: JiytViewModelAnimList = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = JiytAnimListArgs,
    ){
        // ANIMATION LIST
        composable<JiytAnimListArgs>{
            JiytAnimListScreen(
                navToAnimEditor = { jsonEntry: String ->
                    navController.navigate(JiytAnimEditorArgs(jsonEntry))
                }
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
                }
            )
        }
    }
}