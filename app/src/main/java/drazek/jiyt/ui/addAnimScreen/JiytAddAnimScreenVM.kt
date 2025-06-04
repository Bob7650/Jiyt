package drazek.jiyt.ui.addAnimScreen

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import drazek.jiyt.util.ToolTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

private const val HISTORY_LIMIT = 50

class JiytAddAnimScreenVM: ViewModel() {

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
        //Log.e("popFuture", "Returned Null")
        return null
    }

    fun popHistory(): List<List<Color>>?{
        //Log.e("Drawing", "undo() called!")
        if(_history.size > 1) {
            _future.add(_history.last())
            _history.removeAt(_history.size - 1)
            return _history.last()
        }
        return null
    }

    fun onColorClick(){

    }

    fun onPreviousFrameClick(){

    }

    fun convertToJSON(inGrid: List<List<Color>>): String{
        val serializableGrid: List<List<Int>> = inGrid.map { row ->
            row.map{ color ->
                color.toArgb()
            }
        }

        val json = Gson().toJson(serializableGrid)

        return json
    }

    fun onSaveClick(context: Context, fileName: String, jsonData: String): Boolean{
        try {
            val nameWithExt = "$fileName.json"

            val file = File(context.filesDir, nameWithExt)
            file.writeText(jsonData)

            Log.d("GridSave", jsonData)

            viewModelScope.launch {
                _toastEvent.emit("Saved to ${context.filesDir}")
            }

        } catch (e: IOException){
            return false
        }

        return true
    }

    fun onNextFrameClick(){

    }

    fun saveGridState(listToSave: List<List<Color>>){
        _future.clear()
        //Log.e("saveGridState","Drag ended. Added to history")
        _history.add(listToSave)
        if(_history.size > HISTORY_LIMIT){
            //Log.e("saveGridState","History limit reached. Removing oldest")
            _history.removeAt(0)
        }
    }
}