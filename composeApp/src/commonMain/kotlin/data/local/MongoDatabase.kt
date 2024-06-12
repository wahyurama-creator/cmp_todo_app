package data.local

import ToDoTask
import domain.RequestState
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class MongoDatabase {
    private var realm: Realm? = null

    init {
        configDatabase()
    }

    private fun configDatabase() {
        if (realm == null || realm!!.isClosed()) {
            val config = RealmConfiguration.Builder(
                schema = setOf(ToDoTask::class)
            )
                .compactOnLaunch()
                .build()
            realm = Realm.open(config)
        }
    }

    fun readActiveTasks(): Flow<RequestState<List<ToDoTask>>> {
        return realm?.query(clazz = ToDoTask::class, query = "completed == $0", false)
            ?.asFlow()
            ?.map { result ->
                RequestState.Success(
                    data = result.list.sortedByDescending { task -> task.favorite }
                )
            } ?: flow { RequestState.Error(message = "Realm is not available") }
    }

    fun readCompletedTasks(): Flow<RequestState<List<ToDoTask>>> {
        return realm?.query(clazz = ToDoTask::class, query = "completed == $0", true)
            ?.asFlow()
            ?.map { result ->
                RequestState.Success(data = result.list)
            } ?: flow { RequestState.Error(message = "Realm is not available") }
    }

    suspend fun addTask(task: ToDoTask) {
        println("Add task")
        realm?.write { copyToRealm(task) }
    }

    suspend fun updateTask(task: ToDoTask) {
        println("Update task")
        realm?.write {
            try {
                val queriedTask = query(ToDoTask::class, "_id == $0", task._id)
                    .first()
                    .find()
                queriedTask?.let {
                    findLatest(it)?.let { currentTask ->
                        currentTask.title = task.title
                        currentTask.description = task.description
                    }
                }
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    suspend fun setCompleteTask(task: ToDoTask, taskCompleted: Boolean) {
        realm?.write {
            try {
                val queriesTask = query(clazz = ToDoTask::class, "_id == $0", task._id)
                    .find()
                    .first()
                queriesTask.apply { completed = taskCompleted }
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    suspend fun setFavoriteTask(task: ToDoTask, taskFavorite: Boolean) {
        realm?.write {
            try {
                val queriesTask = query(clazz = ToDoTask::class, "_id == $0", task._id)
                    .find()
                    .first()
                queriesTask.apply { favorite = taskFavorite }
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    suspend fun deleteTask(task: ToDoTask) {
        realm?.write {
            try {
                val queriesTask = query(clazz = ToDoTask::class, "_id == $0", task._id)
                    .first()
                    .find()
                queriesTask?.let {
                    findLatest(it)?.let { currentTask ->
                        delete(currentTask)
                    }
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
}