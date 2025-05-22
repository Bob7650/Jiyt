package drazek.jiyt

import android.Manifest
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanResult
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.application
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class JiytAnimListViewModel(application: Application, private val bluetoothAdapter: BluetoothAdapter) : AndroidViewModel(application) {
    private val _scanResults = MutableLiveData<List<ScanResult>>()
    val scanResults: LiveData<List<ScanResult>> = _scanResults

    private val _permissionsGranted =  MutableStateFlow(false)
    val permissionsGranted: StateFlow<Boolean> = _permissionsGranted


    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun scanForDevices(){
        val bleScanner = bluetoothAdapter.bluetoothLeScanner

        val foundDevices = mutableListOf<ScanResult>()

        JiytBleScanner(application,bleScanner).startScan({ result->
            foundDevices.add(result)
            _scanResults.postValue(foundDevices)
        })
    }

    fun setPermissionsGranted(granted: Boolean){
        _permissionsGranted.value = granted
    }



    //----------------------------factory----------------------------
    companion object {
        class JiytAnimListViewModelFactory(private val application: Application, private val bluetoothAdapter: BluetoothAdapter): ViewModelProvider.AndroidViewModelFactory(application) {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return when {
                    modelClass.isAssignableFrom(JiytAnimListViewModel::class.java) -> {
                        JiytAnimListViewModel(application, bluetoothAdapter) as T
                    }
                    else -> throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}