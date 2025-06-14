package drazek.jiyt.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import drazek.jiyt.R
import drazek.jiyt.ui.data.BluetoothState
import drazek.jiyt.ui.theme.JiytTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JiytTopAppBar(
    title: String,
    canGoBack: Boolean = false,
    onBackClicked: () -> Unit = {},
    canSettings: Boolean = false,
    onBTSettings: () -> Unit = {},
    bluetoothState: BluetoothState = BluetoothState.BT_OFF
){

    var isAnimationRunning by remember { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(),
        title = {
            Text(title)
        },
        navigationIcon = {
            if(canGoBack) {
                IconButton(
                    onClick = {
                        isAnimationRunning = true
                        onBackClicked()
                    },
                    enabled = !isAnimationRunning
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        },
        actions = {
            if(canSettings) {
                IconButton(onClick = {
                    onBTSettings()
                }) {
                    when(bluetoothState){
                        BluetoothState.BT_ON -> {
                            Icon(
                                painter = painterResource(R.drawable.baseline_bluetooth_24),
                                contentDescription = null
                            )
                        }
                        BluetoothState.BT_OFF -> {
                            Icon(
                                painter = painterResource(R.drawable.baseline_bluetooth_disabled_24),
                                contentDescription = null
                            )
                        }
                        BluetoothState.BT_CONNECTED -> {
                            Icon(
                                painter = painterResource(R.drawable.baseline_bluetooth_connected_24),
                                contentDescription = null,
                                tint = Color.Green
                            )
                        }
                    }
                }
            }
        }
    )
}

@Preview
@Composable
private fun PrevTopBar() {
    JiytTheme {
        JiytTopAppBar(title = "Animations",canGoBack = true, canSettings = true)
    }
}