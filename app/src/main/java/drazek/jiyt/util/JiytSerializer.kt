package drazek.jiyt.util

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.google.gson.Gson
import drazek.jiyt.ui.data.JiytAnimListEntry
import kotlinx.serialization.Serializable
import java.io.IOException

class JiytSerializer {
    fun serializeEntry(entry: JiytAnimListEntry): String{

        val serializableData = getSerializableGrid(entry.data)

        val serializableEntry = JiytSerializableEntry(fileName = entry.fileName, data = serializableData)

        return Gson().toJson(serializableEntry)
    }

    // TODO: make loadBitmap and deserializeEntry suspend and load stuff only once in app lifetime
    fun deserializeEntry(json: String, storageManager: JiytStorageManager): JiytAnimListEntry?{
        try{
            val serializableEntry = Gson().fromJson(json, JiytSerializableEntry::class.java)

            if(serializableEntry==null)
                return null

            val grid = serializableEntry.data.map { row ->
                row.map { pixel ->
                    Color(red = pixel[0]/255f, green = pixel[1]/255f, blue = pixel[2]/255f, alpha = 1f)
                }
            }

            val bitmap = storageManager.loadBitmap("${serializableEntry.fileName}.png") ?: JiytPreviewMaker().getDefaultPreview()

            return JiytAnimListEntry(
                fileName = serializableEntry.fileName,
                data = grid,
                prevImage = bitmap
            )
        }catch(e: IOException){
            Log.e("JiytSerializer","Incompatible JSON")
            return null
        }
    }

    fun getSerializableGrid(grid: List<List<Color>>): List<List<List<Int>>>{
        return grid.map { row ->
            row.map { color ->
                listOf(
                    (color.red * 255).toInt(),
                    (color.green * 255).toInt(),
                    (color.blue * 255).toInt()
                )
            }
        }
    }
}

@Serializable
private data class JiytSerializableEntry(
    val fileName: String,
    val data: List<List<List<Int>>>,
)