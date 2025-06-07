package drazek.jiyt.ui.animListScreen

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import drazek.jiyt.ui.data.JiytAnimListEntry
import drazek.jiyt.util.JiytStorageManager
import java.io.File

class JiytAnimListVM(storageManager: JiytStorageManager): ViewModel() {

    private val _animListEntries = mutableStateListOf<JiytAnimListEntry>()
    val animListEntries: List<JiytAnimListEntry> get() = _animListEntries

    fun updateEntriesFromStorage(context: Context, subDirName: String = "animations"){
        // Creating reference to subdirectory
        val subDir = File(context.filesDir, subDirName)

        // If subdirectory does not exist or it's not a directory end here
        if(!(subDir.exists() && subDir.isDirectory)) {
            Log.d("updateEntries", "Sub-directory \'$subDir\' does not exist")
            return
        }

        // Get all names
        val listOfFiles = subDir.listFiles()?.map{
            it.name
        }?.toMutableList() ?: mutableListOf()

        // Remove names that we already store
        _animListEntries.forEach {
            listOfFiles.remove(it.fileName)
        }

        // Turn files into instances of JiytAnimListEntry
        if(listOfFiles.isNotEmpty()) {

            // Map only correct entries to loadedEntries (ones that can be successfully converted)
            val loadedEntries = listOfFiles.mapNotNull { fileName ->
                try {
                    Gson().fromJson(
                        getFileContentsByName(fileName, context),
                        JiytAnimListEntry::class.java
                    )
                } catch (e: Exception) {
                    Log.d("loadEntries", "Skipping $fileName")
                    null
                }
            }

            // Add all created instances to our list
            loadedEntries.forEach {
                _animListEntries.add(it)
            }
            Log.d("updateEntries", "Loaded data from $subDir")
        }

    }

    fun getFileContentsByName(fileName: String, context: Context, subDirName: String = "animations"): String{
        // Get reference to a file
        val file = File(File(context.filesDir, subDirName), fileName)

        // Read data as string
        val data = file.readText()

        return data
    }
}

class JiytAnimListVMFactory(private val context: Context): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val storageManager = JiytStorageManager(context)
        if(modelClass.isAssignableFrom(JiytAnimListVM::class.java)){
            return JiytAnimListVM(storageManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}