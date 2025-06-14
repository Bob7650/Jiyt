package drazek.jiyt.util

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class JiytBluetoothStateListener(private val bluetoothUtil: JiytBluetoothUtil): BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)){
            BluetoothAdapter.STATE_ON -> {

            }
            BluetoothAdapter.STATE_OFF -> {
                bluetoothUtil.terminateConnection()
            }
        }
    }
}