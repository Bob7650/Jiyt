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
import drazek.jiyt.ui.addAnimScreen.JiytAnimEditorArgs
import drazek.jiyt.ui.addAnimScreen.JiytAnimEditorScreen
import drazek.jiyt.ui.animListScreen.JiytAnimListArgs
import drazek.jiyt.ui.animListScreen.JiytAnimListScreen
import drazek.jiyt.ui.data.JiytAnimListEntry
import kotlinx.serialization.Serializable

@Composable
fun JiytMainScreen(
    viewModel: JiytViewModelAnimList = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = JiytAnimListArgs,
    ){
        composable<JiytAnimListArgs>{
            JiytAnimListScreen(
                navToAnimEditor = { jsonEntry: String ->
                    navController.navigate(JiytAnimEditorArgs(jsonEntry))
                }
            )
        }
        composable<JiytAnimEditorArgs> {
            val args = it.toRoute<JiytAnimEditorArgs>()

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