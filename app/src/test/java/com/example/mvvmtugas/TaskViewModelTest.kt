package com.example.mvvmtugas

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskViewModelTest {
    // A fake repository gives us control over the starting data

    private class FakeRepository : TaskRepository {
        private var savedTasks = listOf(
            Task(1, "First"),
            Task(2, "Second", done = true)
        )

        override fun loadTasks(): List<Task> {
            return savedTasks
        }

        override fun saveTasks(tasks: List<Task>) {
            savedTasks = tasks
        }
    }

    @Test
    fun toggleDone_flipsTheTaskState() {
        val viewModel = TaskViewModel(FakeRepository())
        viewModel.toggleDone(1)
        assertTrue(viewModel.tasks.value.first { it.id == 1 }.done)
    }
    @Test
    fun toggleDone_twice_returnsToOriginalState() {
        val viewModel = TaskViewModel(FakeRepository())
        viewModel.toggleDone(2)
        viewModel.toggleDone(2)
        assertTrue(viewModel.tasks.value.first { it.id == 2 }.done)
    }
    @Test
    fun addTask_appendsWithNextId() {
        val viewModel = TaskViewModel(FakeRepository())
        viewModel.addTask("Third")
        assertEquals(3, viewModel.tasks.value.size)
        assertEquals(3, viewModel.tasks.value.last().id)
        assertEquals("Third", viewModel.tasks.value.last().title)
    }
    @Test
    fun addTask_ignoresBlankTitle() {
        val viewModel = TaskViewModel(FakeRepository())
        viewModel.addTask(" ")
        assertEquals(2, viewModel.tasks.value.size)
    }
}

