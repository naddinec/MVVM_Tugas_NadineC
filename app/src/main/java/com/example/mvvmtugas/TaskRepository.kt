package com.example.mvvmtugas

interface TaskRepository {
    fun loadTasks(): List<Task>
    fun saveTasks(tasks: List<Task>)
}