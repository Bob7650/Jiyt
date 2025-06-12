package drazek.jiyt.ui

import androidx.lifecycle.ViewModel
import drazek.jiyt.ui.animeditor.JiytAnimEditorVMFactory
import drazek.jiyt.ui.animlist.JiytAnimListVMFactory
import drazek.jiyt.ui.btscreen.JiytBTScreenVMFactory
import drazek.jiyt.util.JiytBluetoothUtil
import drazek.jiyt.util.JiytStorageManager

class JiytMainScreenVM() : ViewModel() {

    fun getJiytAnimEditorVMFactory(storageManager: JiytStorageManager): JiytAnimEditorVMFactory{
        return JiytAnimEditorVMFactory(storageManager)
    }

    fun getJiytAnimListViewVMFactory(storageManager: JiytStorageManager, bluetoothUtil: JiytBluetoothUtil): JiytAnimListVMFactory{
        return JiytAnimListVMFactory(storageManager, bluetoothUtil)
    }

    fun getJiytBTScreenVMFactory(bluetoothUtil: JiytBluetoothUtil): JiytBTScreenVMFactory{
        return JiytBTScreenVMFactory(bluetoothUtil)
    }
}