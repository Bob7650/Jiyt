package drazek.jiyt

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import drazek.jiyt.ui.addAnimScreen.JiytAnimEditorScreen
import drazek.jiyt.ui.animListScreen.JiytAnimListScreen
import drazek.jiyt.ui.JiytViewModelAnimList
import drazek.jiyt.ui.addAnimScreen.JiytAnimEditorArgs
import drazek.jiyt.util.JiytAnimListEntry
import kotlinx.serialization.Serializable
import kotlin.collections.List

enum class JiytMainScreen{
    Home,
    AnimEditor
}

// TODO: add top app bar here so u dont have to pass data through two composables
// TODO: finish the navigation
@Composable
fun JiytMainScreen(
    viewModel: JiytViewModelAnimList = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = ScreenA,
    ){
        composable<ScreenA>{
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

@Serializable
object ScreenA