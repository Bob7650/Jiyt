package drazek.jiyt.ui.btscreen

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.companion.CompanionDeviceManager
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import drazek.jiyt.ui.components.JiytTopAppBar
import kotlinx.coroutines.withContext

@Composable
fun JiytBTSettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: JiytBTSettingsVM,
    navToList: () -> Unit,
    )
{
    val context = LocalContext.current

    // Bonded list
    val bondedDevices = viewModel.bondedDevices

    // Bluetooth device
    var connectedDeviceName = viewModel.bluetoothUtil.connectedDeviceName


    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if(!granted){
            // TODO: Show message explaining why the app needs this permission
        }else{
            viewModel.updateBondedDevices(context)
        }
    }

    val associationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if(result.resultCode == Activity.RESULT_OK){
            val device = result.data?.getParcelableExtra<BluetoothDevice>(CompanionDeviceManager.EXTRA_DEVICE)
            if(device != null)
                viewModel.bluetoothUtil.openSocket(device)
        }
    }

    LaunchedEffect(Unit) {
        if(ActivityCompat.checkSelfPermission(context,android.Manifest.permission.BLUETOOTH_CONNECT)== PackageManager.PERMISSION_GRANTED){
            viewModel.updateBondedDevices(context)
        }else {
            permissionLauncher.launch(android.Manifest.permission.BLUETOOTH_CONNECT)
        }
    }




    Scaffold(
        topBar = { JiytTopAppBar(title = "Bluetooth", canGoBack = true, onBackClicked = navToList) }
    ) { contentPadding ->

        Column(modifier = Modifier.padding(contentPadding)) {

            // CONNECTED TO
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
                    Text("Connected device: ")
                    if (connectedDeviceName.isEmpty()) {
                        Text(
                            text = "none",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = connectedDeviceName,
                            color = Color.Green,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // NEW DEVICE + REFRESH BUTTONS
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp)
            ) {

                // NEW DEVICE
                Button(onClick = {
                    // Check and ask for permissions
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.BLUETOOTH_CONNECT
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        // For now just run Companion Manager
                        // Run association process
                        viewModel.bluetoothUtil.runCompanionManagerWindow(
                            launcher = associationLauncher,
                            context = context
                        )
                    } else {
                        permissionLauncher.launch(android.Manifest.permission.BLUETOOTH_CONNECT)
                    }
                }) {
                    Text("Connect new device")
                }

                // REFRESH
                Button(onClick = {
                    viewModel.updateBondedDevices(context)
                }) {
                    Text("Refresh List")
                }

            }

            // DEVICES LAZY COLUMN
            LazyColumn {
                items(bondedDevices){
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp)
                    ) {
                        // DEVICE NAME
                        Text(it.name)

                        // CONNECT
                        Button(
                            onClick = {
                                viewModel.bluetoothUtil.openSocket(it)
                            }
                        ) {
                            Text("Connect")
                        }
                    }
                }
            }
        }

    }
}