
package com.example.mvvmtugas

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONArray
import org.json.JSONObject

class TaskViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _tasks =
        MutableStateFlow(repository.loadTasks())

    val tasks: StateFlow<List<Task>> =
        _tasks.asStateFlow()

    fun toggleDone(taskId: Int) {
        _tasks.update { list ->
            list.map { task ->
                if (task.id == taskId) {
                    task.copy(done = !task.done)
                } else {
                    task
                }
            }
        }
        repository.saveTasks(_tasks.value)
    }

    fun addTask(title: String) {
        if (title.isBlank()) return

        _tasks.update { list ->
            val nextId = (list.maxOfOrNull { it.id } ?: 0) + 1
            list + Task(nextId, title.trim())
        }
        repository.saveTasks(_tasks.value)
    }
}

class TaskViewModelFactory(
    private val repository: TaskRepository
) : androidx.lifecycle.ViewModelProvider.Factory {

    override fun <T : androidx.lifecycle.ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class SharedPreferencesTaskRepository(
    context: Context
) : TaskRepository {

    private val preferences = context.getSharedPreferences(
        "task_storage",
        Context.MODE_PRIVATE
    )

    override fun loadTasks(): List<Task> {
        val savedData = preferences.getString("tasks", null)
            ?: return emptyList()

        return try {
            val jsonArray = JSONArray(savedData)

            List(jsonArray.length()) { index ->
                val item = jsonArray.getJSONObject(index)
                Task(
                    id = item.getInt("id"),
                    title = item.getString("title"),
                    done = item.getBoolean("done")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun saveTasks(tasks: List<Task>) {
        val jsonArray = JSONArray()

        tasks.forEach { task ->
            val item = JSONObject().apply {
                put("id", task.id)
                put("title", task.title)
                put("done", task.done)
            }
            jsonArray.put(item)
        }

        preferences.edit()
            .putString("tasks", jsonArray.toString())
            .apply()
    }
}