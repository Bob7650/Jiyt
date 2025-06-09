package drazek.jiyt.ui.animListScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import drazek.jiyt.util.JiytStorageManager

class JiytAnimListVM(val storageManager: JiytStorageManager): ViewModel()


/**
 * FACTORY
**/

class JiytAnimListVMFactory(private val context: Context): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val storageManager = JiytStorageManager(context)
        if(modelClass.isAssignableFrom(JiytAnimListVM::class.java)){
            return JiytAnimListVM(storageManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}