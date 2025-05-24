package drazek.jiyt

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.companion.CompanionDeviceManager
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import drazek.jiyt.ui.AnimListScreen
import drazek.jiyt.ui.theme.JiytTheme

class MainActivity : ComponentActivity() {

    private val animViewModel: JiytViewModelAnimList by viewModels()
    private val permissions = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT
    )


    //** LAUNCHERS **//
    private var enableBluetoothLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                //bluetooth enabled
                //making sure that there is no broken connection
                animViewModel.terminateConnection()
                Toast.makeText(this, "Enabled bluetooth for you :*", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Unable to activate bluetooth", Toast.LENGTH_LONG).show()
            }
        }

    @SuppressLint("MissingPermission")
    private var askForPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
            val allGranted = granted.all {
                it.value
            }

            if (allGranted) {
                Toast.makeText(this, "${granted.keys} granted", Toast.LENGTH_SHORT).show()
                animViewModel.setPermissionsGranted(true)
                animViewModel.setBondedDevices()
            } else {
                Toast.makeText(this, "Couldn't grant ${granted.keys}", Toast.LENGTH_LONG).show()
            }
        }

    @SuppressLint("MissingPermission")
    private var deviceChooserLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val device =
                    result.data?.getParcelableExtra<BluetoothDevice>(CompanionDeviceManager.EXTRA_DEVICE)

                // Permissions already checked, called from function that requires them
                Log.println(Log.INFO, "Bluetooth", "Connecting to ${device!!.name}")
                animViewModel.connectToDevice(device,this)
            }
        }
    //***************//


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val bluetoothManager = application.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        if (bluetoothManager.adapter == null) {
            //bluetooth not supported on this device
        } else {
            animViewModel.storeBluetoothAdapter(bluetoothManager.adapter)
        }

        askForPermissionsLauncher.launch(permissions)

        setContent {
            MainScreenView()
        }
    }

    @Composable
    fun MainScreenView() {
        JiytTheme {
            AnimListScreen(
                associationOnClick = { associateOnClick() },
                logOnClick = { logOnClick() },
                sendMessageOnClick = { sendMessageOnClick() },
                selectDevice = { name -> selectDevice(name) },
                refreshOnClick = { refreshOnClick() },
                animViewModel
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun refreshOnClick() {
        if (checkAndAskForPermissions()) {
            animViewModel.setBondedDevices()
        }
    }

    private fun sendMessageOnClick() {
        if (animViewModel.sendMessageData()) {
            Toast.makeText(this, "Data sent: \"Hello :D\"", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Unable to send data", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun logOnClick() {
        if (checkAndAskForPermissions()) {
            Toast.makeText(
                this,
                animViewModel.getConnectionName(),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun associateOnClick() {
        if (checkAndAskForPermissions()) {
            makeAssociation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun selectDevice(name: String) {
        if (checkAndAskForPermissions()) {
            animViewModel.bondedDevices.value!!.forEach {
                if (it.name == name) {
                    animViewModel.connectToDevice(it, this)
                    Toast.makeText(this,"Connecting...", Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }
        Toast.makeText(this, "No device $name", Toast.LENGTH_LONG).show()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun makeAssociation() {
        // create association request (what devices you can see)
        animViewModel.createAssociationRequest()

        // creating an async popup that launches a deviceChooserLauncher
        // when user makes a choice or timeout
        val manager = getSystemService(CompanionDeviceManager::class.java)

        manager.associate(
            animViewModel.associationRequest.value!!,
            object : CompanionDeviceManager.Callback() {
                override fun onAssociationPending(intentSender: IntentSender) {
                    deviceChooserLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                }

                override fun onFailure(error: CharSequence?) {
                    Log.e("CDM", "Association failed: $error")
                }
            },
            null
        )
    }

    private fun checkAndAskForPermissions(): Boolean {
        when {
            animViewModel.bluetoothAdapter.value == null -> {
                // bluetooth not supported
                Toast.makeText(
                    this,
                    "Bluetooth is not supported on this device D:",
                    Toast.LENGTH_LONG
                ).show()
            }

            !animViewModel.bluetoothAdapter.value!!.isEnabled -> {
                // bluetooth is off
                Toast.makeText(this, "Bluetooth is off", Toast.LENGTH_LONG).show()
            }

            animViewModel.permissionsGranted.value == null || !animViewModel.permissionsGranted.value!! -> {
                askForPermissionsLauncher.launch(permissions)
            }

            else -> {
                return true
            }
        }
        return false
    }

    private fun checkAndTurnOnBluetooth(animViewModel: JiytViewModelAnimList): Boolean {
        when {
            !animViewModel.bluetoothAdapter.value!!.isEnabled -> {
                enableBluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            }

            else -> {
                return true
            }
        }
        return false
    }


    override fun onDestroy() {
        super.onDestroy()
        animViewModel.terminateConnection()
    }
}