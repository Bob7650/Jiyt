package drazek.jiyt.ui.animlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import drazek.jiyt.util.JiytBluetoothUtil
import drazek.jiyt.util.JiytSerializer
import drazek.jiyt.util.JiytStorageManager

class JiytAnimListVM(val storageManager: JiytStorageManager, val bluetoothUtil: JiytBluetoothUtil): ViewModel() {

    fun sendDataToConnectedDevice(animName: String) {
        val animEntry = JiytSerializer().deserializeEntry(storageManager.getFileContentFromName(fileNameExt = "$animName.json"),storageManager)

        if (animEntry == null) {
            Log.e("sendDataToDevice", "Animation data does not exist!")
            return
        }

        val serializableObject = SerializableObject(
            cmd = "displayFrame",
            frameId = animEntry.fileName,
            frameData = JiytSerializer().getSerializableGrid(animEntry.data)
        )

        val jsonToSend: String = Gson().toJson(serializableObject)

        // bluetooth send
        bluetoothUtil.sendDataToConnected(jsonToSend)
    }
}

data class SerializableObject (
    val cmd: String,
    val frameId: String,
    val frameData: List<List<List<Int>>>
)

/**
 * FACTORY
**/

class JiytAnimListVMFactory(private val storageManager: JiytStorageManager, private val bluetoothUtil: JiytBluetoothUtil): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(JiytAnimListVM::class.java)){
            return JiytAnimListVM(storageManager, bluetoothUtil) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}