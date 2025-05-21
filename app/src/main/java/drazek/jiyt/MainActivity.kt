package drazek.jiyt

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
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
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import drazek.jiyt.ui.components.JiytModalDrawerSheet
import drazek.jiyt.ui.components.JiytTopAppBar
import drazek.jiyt.ui.theme.JiytTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    var resultPermissionRequestLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ accepted ->
        println(accepted)
    }

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
                                if(ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_CONNECT)==PERMISSION_GRANTED)
                                    setupBluetooth()
                                else
                                    resultPermissionRequestLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
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

    var resultBluetoothLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == RESULT_OK){
            //bluetooth available
            Toast.makeText(this,"Bluetooth enabled",Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(this,"Canceled",Toast.LENGTH_LONG).show()
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun setupBluetooth(){
        val bluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

        if(bluetoothAdapter == null){
            //no bluetooth support TODO: inform user what happened
            return
        }

        if(!bluetoothAdapter.isEnabled){
            resultBluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
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