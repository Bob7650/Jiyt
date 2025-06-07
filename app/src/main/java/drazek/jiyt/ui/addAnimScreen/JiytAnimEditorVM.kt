package drazek.jiyt.ui.addAnimScreen

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import drazek.jiyt.util.JiytAnimListEntry
import drazek.jiyt.util.ToolTypes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

private const val HISTORY_LIMIT = 50

class JiytAnimEditorVM: ViewModel() {

    private val _tool = MutableStateFlow(ToolTypes.Pen)
    val tool: StateFlow<ToolTypes> = _tool.asStateFlow()

    private val _history = mutableStateListOf(List(16){ List(16) { Color.Black } })
    val history: List<List<List<Color>>> get() = _history

    private val _future = mutableStateListOf<List<List<Color>>>()
    val future: List<List<List<Color>>> get() = _future

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    fun onPenClick(){
        _tool.value = ToolTypes.Pen
    }

    fun onEraserClick(){
        _tool.value = ToolTypes.Eraser
    }

    fun popFuture(): List<List<Color>>?{
        if(_future.isNotEmpty()){
            _history.add(_future.last())
            _future.removeAt(_future.size-1)
            return _history.last()
        }
        return null
    }

    fun popHistory(): List<List<Color>>?{

        // Checking if history has more than 1 (one) entry
        if(_history.size > 1) {

            // Adding current state to future so user can go back to it if needed
            _future.add(_history.last())

            // Removing current state from history
            _history.removeAt(_history.size - 1)

            // Returning previous state to display
            return _history.last()
        }

        return null

    }

    fun onPreviousFrameClick(){

    }

    fun saveDataToFile(context: Context, fileName: String, data: List<List<Color>>){

        // Sending data to serializable structure
        // 1. Converting Color because it's not serializable
        val serializableGrid: List<List<List<Int>>> = data.map { row ->
            row.map { color ->
                listOf((color.green*255).toInt(), (color.red*255).toInt(), (color.blue*255).toInt())
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
        Log.d("onSaveClick", jsonData)

        // Creating file with filesDir destination
        val file = File(context.filesDir, fileName)

        // Writing to file
        file.writeText(jsonData)

        // Display toast
        sendToastSignal("Saved!")
    }

    fun onNextFrameClick(){

    }

    fun saveGridState(listToSave: List<List<Color>>){

        // Clearing future
        _future.clear()

        // Adding current position to history
        _history.add(listToSave)

        // Checking if exceeding history size and removing oldest entry when true
        if(_history.size > HISTORY_LIMIT){
            _history.removeAt(0)
        }

    }

    fun nameAvailable(name: String, context: Context): Boolean{
        var files: Array<String> = context.fileList()

        return name !in files
    }

    fun sendToastSignal(text: String){
        viewModelScope.launch {
            _toastEvent.emit(text)
        }
    }
}