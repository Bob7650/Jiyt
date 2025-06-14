package drazek.jiyt.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.google.gson.Gson
import drazek.jiyt.ui.data.JiytAnimListEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class JiytStorageManager(context: Context) {

    private val appContext = context.applicationContext

    val animDir = File(appContext.filesDir, "animations")
    val prevDir = File(appContext.filesDir, "previews")

    init{
        // Setting up the directories
        if(!animDir.exists()){
            animDir.mkdirs()
        }

        if(!prevDir.exists()){
            prevDir.mkdirs()
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
            JiytSerializer().deserializeEntry(getFileContentFromName(fileName), storageManager = this)
        }

        // Add all created instances to our list
        _animListEntries.addAll(loadedEntries)
    }

    fun saveDataToFile(entry: JiytAnimListEntry){
        try {
            Log.d("StorageManager", "Saving ${entry.fileName} to $animDir")

            // Setting preview name
            val prevName = "${entry.fileName}.png"
            val fileName = "${entry.fileName}.json"

            // Converting to json
            val json = JiytSerializer().serializeEntry(entry)

            // Creating file with filesDir destination
            val file = File(animDir, fileName)

            // Writing to file
            file.writeText(json)

            // Make bitmap for preview
            val previewImage = JiytPreviewMaker().makeABitmapFor(entry.data)

            // Save a preview to a file
            saveBitmapToInternalStorage(previewImage, prevName)

        }catch (e: Exception){
            Log.e("StorageManager", "Unable to save ${entry.fileName}.json: ${e.message}")
        }
    }

    fun checkIfNameAvailable(name: String): Boolean{
        return name !in listFilesOnDevice()
    }

    // Returns contents of the file
    fun getFileContentFromName(fileNameExt: String): String{
        var data = ""
        try {
            Log.d("StorageManager", "Loading data from $fileNameExt")
            // Get reference to a file
            val file = File(animDir, fileNameExt)

            // Read data as string
            data = file.readText()
        }catch (e: IOException){
            Log.e("StorageManager", "Unable to load content from $fileNameExt: ${e.message}")
        }
        return data
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

    fun saveBitmapToInternalStorage(bitmap: Bitmap, filenameExt: String): Boolean {
        return try {

            val file = File(prevDir, filenameExt)

            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }

            true
        } catch (e: Exception) {
            Log.e("StorageManager","Couldn't save bitmap $filenameExt: ${e.message}")
            false
        }
    }

    fun loadBitmap(filenameExt: String): Bitmap?{
        return try {
            val file = File(prevDir, filenameExt)
            if (file.exists()) {
                BitmapFactory.decodeFile(file.absolutePath)
            } else {
                null // File not found
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     *  PRIVATE FUNCTIONS
    **/

    private fun listFilesOnDevice(): MutableList<String>{
        return animDir.list()?.toMutableList() ?: mutableListOf()
    }
}