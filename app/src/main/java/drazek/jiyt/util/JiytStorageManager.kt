package drazek.jiyt.util

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import com.google.gson.Gson
import drazek.jiyt.ui.data.JiytAnimListEntry
import java.io.File

class JiytStorageManager(context: Context) {

    val animDir = File(context.filesDir, "animations")

    init{
        if(!animDir.exists()){
            animDir.mkdir()
        }
    }

    private val _animListEntries = mutableStateListOf<JiytAnimListEntry>()
    val animListEntries: List<JiytAnimListEntry> get() = _animListEntries

    fun updateEntriesFromStorage(){

        // Get all names
        val listOfFiles = animDir.listFiles()?.map{
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
                        getFileContentsByName(fileName),
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
            Log.d("updateEntries", "Loaded data from $animDir")
        }

    }

    fun getFileContentsByName(fileName: String): String{
        // Get reference to a file
        val file = File(animDir, fileName)

        // Read data as string
        val data = file.readText()

        return data
    }

    fun saveDataToFile(fileName: String, data: List<List<Color>>): Boolean{
        try {

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
            return false
        }
        return true
    }

}