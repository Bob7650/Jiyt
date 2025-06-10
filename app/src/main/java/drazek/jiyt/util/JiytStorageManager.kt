package drazek.jiyt.util

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import com.google.gson.Gson
import drazek.jiyt.ui.data.JiytAnimListEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class JiytStorageManager(context: Context) {

    // Directory where animations are stored
    val animDir = File(context.filesDir, "animations")

    init{
        if(!animDir.exists()){
            animDir.mkdir()
        }
    }

    // Variable with entries exposed to Compose screens
    private val _animListEntries = mutableStateListOf<JiytAnimListEntry>()
    val animListEntries: List<JiytAnimListEntry> get() = _animListEntries

    fun updateEntriesFromStorage(){

        Log.d("StorageManager", "Updating entries from $animDir")

        _animListEntries.clear()

        // Get all names
        val listOfFiles = listFilesOnDevice()

        // Map only correct entries to loadedEntries (ones that can be successfully converted)
        val loadedEntries = listOfFiles.mapNotNull { fileName ->
            getEntryClassFromName(fileName)
        }

        // Add all created instances to our list
        _animListEntries.addAll(loadedEntries)
    }

    fun saveDataToFile(fileName: String, data: List<List<Color>>){
        try {
            Log.d("StorageManager", "Saving $fileName to $animDir")

            // Sending data to serializable structure
            // 1. Converting Color because it's not serializable
            val serializableGrid: List<List<List<Int>>> = data.map { row ->
                row.map { color ->
                    listOf(
                        (color.green * 255).toInt(),
                        (color.red * 255).toInt(),
                        (color.blue * 255).toInt()
                    )
                }
            }

            // 2. Saving to custom structure
            val animListEntry = JiytAnimListEntry(
                fileName = fileName,
                frameRate = 5.0,
                data = serializableGrid
            )

            // Converting to json
            val jsonData = Gson().toJson(animListEntry)

            // Creating file with filesDir destination
            val file = File(animDir, fileName)

            // Writing to file
            file.writeText(jsonData)

        }catch (e: Exception){
            Log.e("StorageManager", "Unable to save $fileName: ${e.message}")
        }
    }

    fun checkIfNameAvailable(name: String): Boolean{
        return name !in listFilesOnDevice()
    }

    // Returns contents of the file
    fun getFileDataFromName(fileName: String): String{
        var data = ""
        try {
            Log.d("StorageManager", "Loading data from $fileName")
            // Get reference to a file
            val file = File(animDir, fileName)

            // Read data as string
            data = file.readText()
        }catch (e: IOException){
            Log.e("StorageManager", "Unable to load data from $fileName")
        }
        return data
    }

    // Returns a JiytAnimListEntry class or null
    fun getEntryClassFromName(fileName: String): JiytAnimListEntry?{

        var entryObject: JiytAnimListEntry?

        try {
            Log.d("StorageManager", "Converting $fileName to a JiytAnimListEntry")
            // Using Gson library to convert json back to object
            entryObject = Gson().fromJson(
                getFileDataFromName(fileName),
                JiytAnimListEntry::class.java
            )
        } catch (e: Exception) {
            Log.e("StorageManager", "Unable to transform $fileName to JiytAnimListEntry. Skipping! Exception: ${e.message}")
            return null
        }

        return entryObject
    }

    // Needs to be launched in scope. Code after it can only be executed after this function finishes
    // Removes file from the storage
    suspend fun removeFileFromStorage(fileName: String){
        withContext(Dispatchers.IO) {
            try {
                Log.d("StorageManager", "Removing file $fileName from storage")
                // Remove file from storage
                File(animDir, fileName).delete()
            } catch (e: IOException) {
                Log.e("StorageManager", "Failed to remove the file: ${e.message}")
            }finally {
                // Update entries always (coz why not)
                updateEntriesFromStorage()
            }
        }
    }

    /**
     *  PRIVATE FUNCTIONS
    **/

    private fun listFilesOnDevice(): MutableList<String>{
        return animDir.list()?.toMutableList() ?: mutableListOf()
    }
}