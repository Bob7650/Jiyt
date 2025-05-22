package drazek.jiyt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import drazek.jiyt.ui.components.JiytModalDrawerSheet
import drazek.jiyt.ui.components.JiytTopAppBar
import drazek.jiyt.ui.theme.JiytTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            JiytTheme {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        JiytModalDrawerSheet()
                    },
                ) {
                    Scaffold(
                        topBar = {
                            JiytTopAppBar(onClick = {
                                if (it == "menu"){
                                    scope.launch {
                                        drawerState.apply {
                                            if (isClosed) open() else close()
                                        }
                                    }
                                }
                            })
                        }
                    ) { contentPadding ->
                        Button(
                            onClick = {

                            },
                            modifier = Modifier.padding(contentPadding)
                        ) {
                            Text("Send Data")
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun PrevButton() {
        JiytTheme {
            Button(
                onClick = {},
                modifier = Modifier.padding()
            ) {
                Text("Send Data")
            }
        }
    }
}