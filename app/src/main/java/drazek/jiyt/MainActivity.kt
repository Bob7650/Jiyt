package drazek.jiyt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import drazek.jiyt.ui.theme.JiytTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreenView()
        }
    }

    @Composable
    fun MainScreenView() {
        JiytTheme {
            JiytMainScreen()
        }
    }

}