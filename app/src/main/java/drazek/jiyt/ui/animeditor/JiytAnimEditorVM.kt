package drazek.jiyt.ui.animeditor

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import drazek.jiyt.util.JiytStorageManager
import drazek.jiyt.ui.data.ToolTypes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val HISTORY_LIMIT = 50

class JiytAnimEditorVM(val storageManager: JiytStorageManager): ViewModel() {

    private val _tool = MutableStateFlow(ToolTypes.Pen)
    val tool: StateFlow<ToolTypes> = _tool.asStateFlow()

    private val _history = mutableStateListOf(List(16){ List(16) { Color.Black } })
    val history: List<List<List<Color>>> get() = _history

    private val _future = mutableStateListOf<List<List<Color>>>()
    val future: List<List<List<Color>>> get() = _future

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    // Change tool to pen
    fun onPenClick(){
        _tool.value = ToolTypes.Pen
    }

    // Change tool to eraser
    fun onEraserClick(){
        _tool.value = ToolTypes.Eraser
    }

    // Make a step forward
    fun popFuture(): List<List<Color>>?{
        // Check if future not empty
        if(_future.isNotEmpty()){
            // Make future our present
            _history.add(_future.last())
            // Forget about our future
            _future.removeAt(_future.size-1)
            // Send present to display
            return _history.last()
        }
        return null
    }

    // Make a step back
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

    fun onNextFrameClick(){

    }

    // This is called when user finishes their action (e.g. stroke or tap[not implemented yet])
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

    // Sends a signal to display a toast to Compose side of things
    fun sendToastSignal(text: String){
        viewModelScope.launch {
            _toastEvent.emit(text)
        }
    }
}

/**
 * FACTORY
 **/

class JiytAnimEditorVMFactory(private val storageManager: JiytStorageManager): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(JiytAnimEditorVM::class.java)){
            return JiytAnimEditorVM(storageManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}