package drazek.jiyt.ui.animListScreen

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import java.io.File

class JiytAnimListVM: ViewModel() {

    fun fetchFilesFromStorage(context: Context): Array<String>{
        return context.fileList()
    }

    fun getFileContentsByName(fileName: String, context: Context): String{
        val file = File(context.filesDir, fileName)

        val data = file.readText()

        Log.d("getFileContentsByName", data)
        return data
    }
}