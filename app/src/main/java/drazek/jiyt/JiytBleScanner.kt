package drazek.jiyt

import android.Manifest
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class JiytBleScanner(private val context: Context, private val bluetoothLeScanner: BluetoothLeScanner) {

    private var scanning = false
    private var scanCallback: ScanCallback? = null


    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startScan(callback: (ScanResult) -> Unit, durationMillis: Long = 10_000){
        if(scanning) return

        scanning = true
        scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                callback(result!!)
            }

            override fun onScanFailed(errorCode: Int) {
                Log.e("BleScanner", "Scan failed with code $errorCode")
            }
        }

        bluetoothLeScanner.startScan(scanCallback)

        CoroutineScope(Dispatchers.Default).launch {
            delay(durationMillis)
            stopScan()
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScan(){
        if(scanning && scanCallback != null){
            bluetoothLeScanner.stopScan(scanCallback)
            scanCallback = null
            scanning = false
        }
    }

}