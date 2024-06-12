import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.local.MongoDatabase
import domain.TaskAction
import kotlinx.coroutines.launch

class TaskViewModel(private val mongoDatabase: MongoDatabase) : ScreenModel {

    fun setAction(action: TaskAction) {
        when (action) {
            is TaskAction.Add -> addTask(action.task)
            is TaskAction.Update -> updateTask(action.task)
            else -> {}
        }
    }

    private fun addTask(task: ToDoTask) {
        screenModelScope.launch {
            mongoDatabase.addTask(task)
        }
    }

    private fun updateTask(task: ToDoTask) {
        screenModelScope.launch {
            mongoDatabase.updateTask(task)
        }
    }
}