package presentation.screen.home

import ToDoTask
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.local.MongoDatabase
import domain.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

typealias MutableTasks = MutableState<RequestState<List<ToDoTask>>>
typealias Tasks = MutableState<RequestState<List<ToDoTask>>>

class HomeViewModel(private val mongoDatabase: MongoDatabase) : ScreenModel {
    private var _activeTasks: MutableTasks = mutableStateOf(RequestState.Idle)
    val activeTasks: Tasks = _activeTasks

    private var _completedTasks: MutableTasks = mutableStateOf(RequestState.Idle)
    val completedTasks: Tasks = _completedTasks

    init {
        _activeTasks.value = RequestState.Loading
        _completedTasks.value = RequestState.Loading

        screenModelScope.launch(Dispatchers.Main) {
            delay(500)
            mongoDatabase.readActiveTasks().collectLatest {
                _activeTasks.value = it
            }
        }
        screenModelScope.launch(Dispatchers.Main) {
            delay(500)
            mongoDatabase.readCompletedTasks().collectLatest {
                _completedTasks.value = it
            }
        }
    }

    fun deleteTask(task: ToDoTask) {
        screenModelScope.launch {
            mongoDatabase.deleteTask(task)
        }
    }

    fun completeTask(task: ToDoTask, completed: Boolean) {
        screenModelScope.launch {
            mongoDatabase.setCompleteTask(task, completed)
        }
    }

    fun favoriteTask(task: ToDoTask, favorite: Boolean) {
        screenModelScope.launch {
            mongoDatabase.setFavoriteTask(task, favorite)
        }
    }
}