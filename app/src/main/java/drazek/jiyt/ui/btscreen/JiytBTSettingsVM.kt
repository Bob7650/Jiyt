package drazek.jiyt.ui.btscreen

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.mutableStateListOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import drazek.jiyt.util.JiytBluetoothUtil

class JiytBTSettingsVM(val bluetoothUtil: JiytBluetoothUtil): ViewModel(){

    val bondedDevices = mutableStateListOf<BluetoothDevice>()

    fun updateBondedDevices(context: Context){
        if(ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED){
            bondedDevices.clear()
            bondedDevices.addAll(bluetoothUtil.getBondedDevices())
        }
    }
}

/**
 * FACTORY
 **/

class JiytBTScreenVMFactory(private val bluetoothUtil: JiytBluetoothUtil): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(JiytBTSettingsVM::class.java)){
            return JiytBTSettingsVM(bluetoothUtil) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}