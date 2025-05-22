package drazek.jiyt

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import drazek.jiyt.ui.components.JiytModalDrawerSheet
import drazek.jiyt.ui.components.JiytTopAppBar
import drazek.jiyt.ui.theme.JiytTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val permissions = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT
    )

    private var enableBluetoothLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == RESULT_OK){
            //bluetooth enabled
            Toast.makeText(this, "Enabled bluetooth for you :*", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this,"Unable to activate bluetooth",Toast.LENGTH_LONG).show()
        }
    }

    private var askForPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ granted ->
        for(grant in granted){
            if(grant.value){
                Toast.makeText(this,"${grant.key} granted", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"Couldn't grant ${grant.key}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val bluetoothManager = application.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        val factory = JiytAnimListViewModel.Companion.JiytAnimListViewModelFactory(application, bluetoothAdapter)
        val animViewModel: JiytAnimListViewModel = ViewModelProvider(this, factory)[JiytAnimListViewModel::class.java]

        setContent {
            MainScreenView(animViewModel, bluetoothAdapter)
        }
    }

    @Composable
    fun MainScreenView(viewModel: JiytAnimListViewModel, bluetoothAdapter: BluetoothAdapter) {

        val permissionGranted = remember {
                permissions.all {
                    ContextCompat.checkSelfPermission(
                        this,
                        it
                    ) == PackageManager.PERMISSION_GRANTED
                }
        }
        viewModel.setPermissionsGranted(permissionGranted)

        val granted by viewModel.permissionsGranted.collectAsState()

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
                            when {
                                !bluetoothAdapter.isEnabled -> {
                                    enableBluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                                }
                                !granted -> {
                                    askForPermissionsLauncher.launch(permissions)
                                }
                                else -> {
                                    viewModel.scanForDevices()
                                }
                            }
                        },
                        modifier = Modifier.padding(contentPadding)
                    ) {
                        Text("Start Scanning")
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