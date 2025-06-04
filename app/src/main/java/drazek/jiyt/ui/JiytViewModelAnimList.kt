package drazek.jiyt.ui

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.companion.AssociationRequest
import android.companion.BluetoothDeviceFilter
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID
import java.util.regex.Pattern

class JiytViewModelAnimList : ViewModel() {
    private val _bluetoothAdapter = MutableLiveData<BluetoothAdapter>()
    val bluetoothAdapter: LiveData<BluetoothAdapter> = _bluetoothAdapter

    private val _permissionsGranted =  MutableLiveData(false)
    val permissionsGranted: LiveData<Boolean> = _permissionsGranted

    private val _associationRequest = MutableLiveData<AssociationRequest>()
    val associationRequest: LiveData<AssociationRequest> = _associationRequest

    private val _bondedDevices = MutableLiveData<Set<BluetoothDevice>>(null)
    val bondedDevices: LiveData<Set<BluetoothDevice>> = _bondedDevices

    private val _bondedNames = mutableStateOf<List<String>>(emptyList())
    val bondedNames: State<List<String>> = _bondedNames

    private var socket: BluetoothSocket? = null
    private var connectedDeviceName: String? = null


    fun createAssociationRequest(){
        val deviceFilter: BluetoothDeviceFilter = BluetoothDeviceFilter.Builder()
            .setNamePattern(Pattern.compile(".")) // Optional filter by name
            .build()

        val request = AssociationRequest.Builder()
            .addDeviceFilter(deviceFilter)
            .build()

        _associationRequest.value = request
    }

    fun storeBluetoothAdapter(adapter: BluetoothAdapter){
        _bluetoothAdapter.value = adapter
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connectToDevice(device: BluetoothDevice, context: Context) {
        terminateConnection()
        connectedDeviceName = device.name

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
                socket = device.createRfcommSocketToServiceRecord(uuid)

                socket?.connect()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Successfully connected to $connectedDeviceName", Toast.LENGTH_LONG).show()
                }
            } catch (e: IOException){
                Log.e("Bluetooth", "Connection failed: ${e.message}")
                terminateConnection()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Connection failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun getConnectionName(): String{
        if(connectedDeviceName != null) {
            Log.println(Log.INFO, "Bluetooth", "Connected to $connectedDeviceName")
            return "$connectedDeviceName"
        }else{
            Log.println(Log.INFO, "Bluetooth", "No device connected")
            return "No device connected"
        }
    }

    fun sendMessageData(): Boolean{
        if(socket?.outputStream != null) {
            try {
                socket!!.outputStream!!.write("Hello :D\n".toByteArray())
                socket!!.outputStream!!.flush()
                Log.println(Log.INFO, "Bluetooth", "Data sent")
                return true
            } catch (e: IOException) {
                Log.e("Bluetooth", "Unable to send message: ${e.message}")
                socket!!.close()
                return false
            }
        }else{
            Log.e("Bluetooth","No socket created")
        }
        return false
    }

    fun setPermissionsGranted(granted: Boolean){
        _permissionsGranted.value = granted
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun setBondedDevices(){
        val devs = _bluetoothAdapter.value!!.bondedDevices
        _bondedDevices.value = devs
        _bondedNames.value = devs.map { it.name }
    }

    fun terminateConnection(){
        socket?.close()
        connectedDeviceName = null

        Log.println(Log.INFO,"Bluetooth", "Terminating connection")
    }
}