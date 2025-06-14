package drazek.jiyt.util

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.companion.AssociationRequest
import android.companion.BluetoothDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.content.IntentFilter
import android.content.IntentSender
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import drazek.jiyt.ui.data.BluetoothState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID
import java.util.regex.Pattern

class JiytBluetoothUtil(context: Context) {
    private val appContext = context.applicationContext

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var socket: BluetoothSocket? = null

    var connectedDeviceName by mutableStateOf("")
        private set
    var bluetoothState by mutableStateOf<BluetoothState>(BluetoothState.BT_OFF)
        private set


    fun setup(){
        // Get bluetooth adapter
        val bm = appContext.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bm.adapter

        bluetoothState = BluetoothState.BT_ON

        appContext.registerReceiver(JiytBluetoothStateListener(this), IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    fun runCompanionManagerWindow(
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        context: Context
    ){
        val request = createAssociationRequest()
        val companionManager = context.getSystemService(CompanionDeviceManager::class.java)

        companionManager.associate(
            request,
            object : CompanionDeviceManager.Callback(){
                override fun onAssociationPending(intentSender: IntentSender) {
                    val senderRequest = IntentSenderRequest.Builder(intentSender).build()
                    launcher.launch(senderRequest)
                }

                override fun onFailure(error: CharSequence?) {
                    Log.e("BluetoothUtil", "Association failed: $error")
                    Toast.makeText(appContext,"Association failed!", Toast.LENGTH_LONG).show()
                }
            },
            null
        )
    }

    fun createAssociationRequest(): AssociationRequest{
        val deviceFilter: BluetoothDeviceFilter = BluetoothDeviceFilter.Builder()
            .setNamePattern(Pattern.compile(".")) // Optional filter by name
            .build()

        val request = AssociationRequest.Builder()
            .addDeviceFilter(deviceFilter)
            .build()

        return request
    }

    // Opens a socket with a provided device and sets it as connected
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun openSocket(device: BluetoothDevice) {

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    appContext,
                    "Connecting...",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // Terminate connection if any is established
            terminateConnection()

            try {
                val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
                socket = device.createRfcommSocketToServiceRecord(uuid)

                if(socket?.connect()!=null){
                    connectedDeviceName = device.name
                    bluetoothState = BluetoothState.BT_CONNECTED

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            appContext,
                            "Connected to $connectedDeviceName",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                    listenToDevice(socket!!)
                }else{
                    Log.e("BluetoothUtil", "Could not create a socket")
                    Toast.makeText(appContext, "Can't establish connection", Toast.LENGTH_LONG)
                        .show()
                }
            } catch (e: IOException) {
                Log.e("BluetoothUtil", "Connection failed: ${e.message}")
                // Terminate connection to avoid broken pipe
                terminateConnection()

                withContext(Dispatchers.Main) {
                    Toast.makeText(appContext, "Connection failed!", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    // Sends data to a connected device
    fun sendDataToConnected(data: String){
        CoroutineScope(Dispatchers.IO).launch {
            // Check if output stream exists (is the socket open)
            if (socket?.outputStream != null) {
                try {

                    // Write data to output stream
                    val dataBytes = "$data\n".toByteArray(Charsets.UTF_8)
                    val chunkSize = 256
                    var offset = 0

                    while (offset < dataBytes.size) {
                        val end = minOf(offset + chunkSize, dataBytes.size)
                        socket!!.outputStream!!.write(dataBytes, offset, end - offset)
                        offset = end
                        delay(10)
                    }

                    // Pushing what remains in the stream so that everything is sent
                    socket!!.outputStream!!.flush()

                    Log.println(
                        Log.INFO,
                        "BluetoothUtil",
                        "Sent data to device $connectedDeviceName"
                    )
                    withContext(Dispatchers.Main) {
                        Toast.makeText(appContext, "Data sent", Toast.LENGTH_SHORT).show()
                    }

                    // Success
                } catch (e: IOException) {
                    // Failed to send
                    Log.e("BluetoothUtil", "Unable to send message: ${e.message}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            appContext,
                            "An error occurred while sending a message :(",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                Log.e("BluetoothUtil", "No socket created")
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        appContext,
                        "Can't send data, no device connected!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // Return bonded devices
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun getBondedDevices(): Set<BluetoothDevice>{
        if(bluetoothAdapter==null){
            Log.e("BluetoothUtil", "Can't fetch bonded devices. BluetoothAdapter does not exist!")
            return emptySet()
        }else {
            val devs: Set<BluetoothDevice> = bluetoothAdapter!!.bondedDevices
            return devs
        }
    }

    // Ends connection with a connected device and reset the socket
    fun terminateConnection(){
        CoroutineScope(Dispatchers.IO).launch {
            // Close connection if socket exists
            socket?.close()
            socket = null

            // Remove connected device
            connectedDeviceName = ""
            bluetoothState = BluetoothState.BT_ON
            Log.d("BluetoothUtil", "Connection terminated")
        }
    }

    // Constantly listens to the connected device. Clears connection and sends a message when the target is not responding
    private fun listenToDevice(socket: BluetoothSocket){
        CoroutineScope(Dispatchers.IO).launch {
            val buffer = ByteArray(1024)
            val inputStream = socket.inputStream

            try {
                while (true) {
                    val bytesRead = inputStream.read(buffer)

                    if (bytesRead > 0) {
                        val data = String(buffer, 0, bytesRead)
                        // TODO: what to do on data received
                    }
                }
            } catch (e: IOException) {
                Log.e("BluetoothUtil", "Disconnected: ${e.message}")
                terminateConnection()

                withContext(Dispatchers.Main) {
                    Toast.makeText(appContext, "Disconnected!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}