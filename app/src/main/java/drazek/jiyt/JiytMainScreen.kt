package drazek.jiyt

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import drazek.jiyt.ui.addAnimScreen.JiytAddAnimationScreen
import drazek.jiyt.ui.JiytAnimListScreen
import drazek.jiyt.ui.JiytViewModelAnimList

enum class JiytMainScreen{
    Home,
    MakeAnim
}

// TODO: add top app bar here so u dont have to pass data through two composables
@Composable
fun JiytMainScreen(
    viewModel: JiytViewModelAnimList = viewModel(),
    navController: NavHostController = rememberNavController()
) {
        NavHost(
            navController = navController,
            startDestination = JiytMainScreen.Home.name,
        ){
            composable(route = JiytMainScreen.Home.name){
                JiytAnimListScreen(
                    onAddClicked = {
                        navController.navigate(JiytMainScreen.MakeAnim.name)
                    }
                )
            }
            composable(route = JiytMainScreen.MakeAnim.name) {
                JiytAddAnimationScreen(
                    onBackClicked = {
                        navController.popBackStack()
                    }
                )
            }
        }
}