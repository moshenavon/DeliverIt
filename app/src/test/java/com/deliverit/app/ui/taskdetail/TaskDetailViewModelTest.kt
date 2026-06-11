package com.deliverit.app.ui.taskdetail

import androidx.lifecycle.SavedStateHandle
import com.deliverit.app.MainDispatcherRule
import com.deliverit.app.R
import com.deliverit.app.data.TaskRepository
import com.deliverit.app.model.DeliveryStatus
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

private const val TASK_ID = "task-1"

@OptIn(ExperimentalCoroutinesApi::class)
class TaskDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: TaskRepository
    private val taskFlow = MutableStateFlow<DeliveryTask?>(null)

    @Before
    fun setUp() {
        repository = mockk()
        every { repository.observeTask(TASK_ID) } returns taskFlow
        coEvery { repository.refreshTasks() } returns Result.success(Unit)
    }

    private fun createViewModel() =
        TaskDetailViewModel(repository, SavedStateHandle(mapOf("taskId" to TASK_ID)))

    @Test
    fun `state reflects the observed task`() = runTest {
        val task = sampleTask(id = TASK_ID)
        taskFlow.value = task

        val viewModel = createViewModel()

        assertEquals(task, viewModel.currentState.task)
        assertFalse(viewModel.currentState.isLoading)
    }

    @Test
    fun `Refresh failure sends ShowError event with the exception message`() = runTest {
        coEvery { repository.refreshTasks() } returns Result.failure(IllegalStateException("offline"))

        val viewModel = createViewModel()
        val events = mutableListOf<TaskDetailEvent>()
        val job = launch { viewModel.event.toList(events) }
        runCurrent()

        viewModel.onIntent(TaskDetailIntent.Refresh)
        runCurrent()

        job.cancel()
        assertEquals(listOf(TaskDetailEvent.ShowError(UiText.DynamicString("offline"))), events)
    }

    @Test
    fun `Refresh failure without a message falls back to the localized string`() = runTest {
        coEvery { repository.refreshTasks() } returns Result.failure(IllegalStateException())

        val viewModel = createViewModel()
        val events = mutableListOf<TaskDetailEvent>()
        val job = launch { viewModel.event.toList(events) }
        runCurrent()

        viewModel.onIntent(TaskDetailIntent.Refresh)
        runCurrent()

        job.cancel()
        assertEquals(listOf(TaskDetailEvent.ShowError(UiText.StringResource(R.string.task_detail_refresh_failed))), events)
    }

    @Test
    fun `ChangeStatus with the current status does not call the repository`() = runTest {
        val task = sampleTask(id = TASK_ID, status = DeliveryStatus.IN_TRANSIT)
        taskFlow.value = task

        val viewModel = createViewModel()

        viewModel.onIntent(TaskDetailIntent.ChangeStatus(DeliveryStatus.IN_TRANSIT))
        viewModel.onIntent(TaskDetailIntent.ChangeStatus(DeliveryStatus.IN_TRANSIT))

        coVerify(exactly = 0) { repository.updateTaskStatus(any(), any()) }
    }

    @Test
    fun `ChangeStatus failure sends ShowError event with the exception message`() = runTest {
        coEvery { repository.updateTaskStatus(any(), any()) } returns
            Result.failure(IllegalStateException("denied"))

        val viewModel = createViewModel()
        val events = mutableListOf<TaskDetailEvent>()
        val job = launch { viewModel.event.toList(events) }
        runCurrent()

        viewModel.onIntent(TaskDetailIntent.ChangeStatus(DeliveryStatus.DELIVERED))
        runCurrent()

        job.cancel()
        assertEquals(listOf(TaskDetailEvent.ShowError(UiText.DynamicString("denied"))), events)
    }

    @Test
    fun `ChangeStatus failure without a message falls back to the localized string`() = runTest {
        coEvery { repository.updateTaskStatus(any(), any()) } returns
            Result.failure(IllegalStateException())

        val viewModel = createViewModel()
        val events = mutableListOf<TaskDetailEvent>()
        val job = launch { viewModel.event.toList(events) }
        runCurrent()

        viewModel.onIntent(TaskDetailIntent.ChangeStatus(DeliveryStatus.DELIVERED))
        runCurrent()

        job.cancel()
        assertEquals(listOf(TaskDetailEvent.ShowError(UiText.StringResource(R.string.task_detail_update_status_failed))), events)
    }
}
