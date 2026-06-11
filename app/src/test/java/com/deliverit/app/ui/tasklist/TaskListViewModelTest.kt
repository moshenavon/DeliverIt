package com.deliverit.app.ui.tasklist

import com.deliverit.app.MainDispatcherRule
import com.deliverit.app.R
import com.deliverit.app.data.TaskRepository
import com.deliverit.app.model.DeliveryTask
import com.deliverit.app.sampleTask
import com.deliverit.app.ui.common.UiText
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class TaskListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: TaskRepository
    private val tasksFlow = MutableStateFlow<List<DeliveryTask>>(emptyList())

    @Before
    fun setUp() {
        repository = mockk()
        every { repository.observeTasks() } returns tasksFlow
        coEvery { repository.refreshTasks() } returns Result.success(Unit)
    }

    private fun createViewModel() = TaskListViewModel(repository)

    @Test
    fun `initial state reflects repository tasks once refresh completes`() = runTest {
        val task = sampleTask()
        tasksFlow.value = listOf(task)

        val viewModel = createViewModel()

        val state = viewModel.currentState
        assertEquals(listOf(task), state.tasks)
        assertFalse(state.isLoading)
        assertFalse(state.isRefreshing)
    }

    @Test
    fun `Refresh intent triggers another repository refresh`() = runTest {
        val viewModel = createViewModel()

        viewModel.onIntent(TaskListIntent.Refresh)

        // Once on init, once for the explicit Refresh intent.
        coVerify(exactly = 2) { repository.refreshTasks() }
    }

    @Test
    fun `refresh failure emits ShowError event with the exception message`() = runTest {
        val viewModel = createViewModel()
        val events = mutableListOf<TaskListEvent>()
        val job = launch { viewModel.event.toList(events) }
        runCurrent()

        coEvery { repository.refreshTasks() } returns Result.failure(IllegalStateException("offline"))
        viewModel.onIntent(TaskListIntent.Refresh)
        runCurrent()

        job.cancel()
        assertEquals(listOf(TaskListEvent.ShowError(UiText.DynamicString("offline"))), events)
    }

    @Test
    fun `refresh failure without a message falls back to the localized string`() = runTest {
        val viewModel = createViewModel()
        val events = mutableListOf<TaskListEvent>()
        val job = launch { viewModel.event.toList(events) }
        runCurrent()

        coEvery { repository.refreshTasks() } returns Result.failure(IllegalStateException())
        viewModel.onIntent(TaskListIntent.Refresh)
        runCurrent()

        job.cancel()
        assertEquals(listOf(TaskListEvent.ShowError(UiText.StringResource(R.string.task_list_refresh_failed))), events)
    }

    @Test
    fun `SelectTask intent sends NavigateToDetail event`() = runTest {
        val viewModel = createViewModel()
        val events = mutableListOf<TaskListEvent>()
        val job = launch { viewModel.event.toList(events) }
        runCurrent()

        viewModel.onIntent(TaskListIntent.SelectTask("task-1"))
        runCurrent()

        job.cancel()
        assertEquals(listOf(TaskListEvent.NavigateToDetail("task-1")), events)
    }

    @Test
    fun `CreateTask intent sends NavigateToCreate event`() = runTest {
        val viewModel = createViewModel()
        val events = mutableListOf<TaskListEvent>()
        val job = launch { viewModel.event.toList(events) }
        runCurrent()

        viewModel.onIntent(TaskListIntent.CreateTask)
        runCurrent()

        job.cancel()
        assertEquals(listOf(TaskListEvent.NavigateToCreate), events)
    }
}
