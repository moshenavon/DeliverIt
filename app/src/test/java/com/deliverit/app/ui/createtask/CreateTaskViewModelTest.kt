package com.deliverit.app.ui.createtask

import com.deliverit.app.MainDispatcherRule
import com.deliverit.app.R
import com.deliverit.app.data.TaskRepository
import com.deliverit.app.model.DeliveryStatus
import com.deliverit.app.sampleTask
import com.deliverit.app.ui.common.UiText
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class CreateTaskViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: TaskRepository
    private lateinit var viewModel: CreateTaskViewModel

    @Before
    fun setUp() {
        repository = mockk()
        coEvery { repository.createTask(any(), any(), any()) } returns Result.success(sampleTask())
        viewModel = CreateTaskViewModel(repository)
    }

    @Test
    fun `initial state is empty with no errors`() {
        val state = viewModel.currentState

        assertEquals("", state.itemDescription)
        assertEquals("", state.fromLocation)
        assertEquals("", state.toLocation)
        assertNull(state.descriptionError)
        assertNull(state.fromError)
        assertNull(state.toError)
        assertFalse(state.isSubmitting)
    }

    @Test
    fun `field changed intents update state and clear their own error`() {
        // Trigger validation so all three errors are populated first.
        viewModel.onIntent(CreateTaskIntent.Submit)

        viewModel.onIntent(CreateTaskIntent.DescriptionChanged("Box of books"))
        viewModel.onIntent(CreateTaskIntent.FromChanged("Warehouse A"))
        viewModel.onIntent(CreateTaskIntent.ToChanged("Warehouse B"))

        val state = viewModel.currentState
        assertEquals("Box of books", state.itemDescription)
        assertEquals("Warehouse A", state.fromLocation)
        assertEquals("Warehouse B", state.toLocation)
        assertNull(state.descriptionError)
        assertNull(state.fromError)
        assertNull(state.toError)
    }

    @Test
    fun `Submit with blank fields sets validation errors and does not call repository`() = runTest {
        viewModel.onIntent(CreateTaskIntent.Submit)

        val state = viewModel.currentState
        assertEquals(UiText.StringResource(R.string.create_task_description_required), state.descriptionError)
        assertEquals(UiText.StringResource(R.string.create_task_pickup_required), state.fromError)
        assertEquals(UiText.StringResource(R.string.create_task_dropoff_required), state.toError)
        coVerify(exactly = 0) { repository.createTask(any(), any(), any()) }
    }

    @Test
    fun `Submit trims whitespace and creates task on success`() = runTest {
        coEvery { repository.createTask("Box of books", "Warehouse A", "Warehouse B") } returns
            Result.success(sampleTask(status = DeliveryStatus.PENDING))

        viewModel.onIntent(CreateTaskIntent.DescriptionChanged("  Box of books  "))
        viewModel.onIntent(CreateTaskIntent.FromChanged("  Warehouse A  "))
        viewModel.onIntent(CreateTaskIntent.ToChanged("  Warehouse B  "))

        val events = mutableListOf<CreateTaskEvent>()
        val job = launch { viewModel.event.toList(events) }
        runCurrent()

        viewModel.onIntent(CreateTaskIntent.Submit)
        runCurrent()

        job.cancel()
        assertEquals(listOf(CreateTaskEvent.NavigateBack), events)
        coVerify { repository.createTask("Box of books", "Warehouse A", "Warehouse B") }
        assertFalse(viewModel.currentState.isSubmitting)
    }

    @Test
    fun `Submit failure sends ShowError event with the exception message`() = runTest {
        coEvery { repository.createTask(any(), any(), any()) } returns
            Result.failure(IllegalStateException("network down"))

        fillValidForm()

        val events = mutableListOf<CreateTaskEvent>()
        val job = launch { viewModel.event.toList(events) }
        runCurrent()

        viewModel.onIntent(CreateTaskIntent.Submit)
        runCurrent()

        job.cancel()
        assertEquals(listOf(CreateTaskEvent.ShowError(UiText.DynamicString("network down"))), events)
        assertFalse(viewModel.currentState.isSubmitting)
    }

    @Test
    fun `Submit failure without a message falls back to the localized string`() = runTest {
        coEvery { repository.createTask(any(), any(), any()) } returns
            Result.failure(IllegalStateException())

        fillValidForm()

        val events = mutableListOf<CreateTaskEvent>()
        val job = launch { viewModel.event.toList(events) }
        runCurrent()

        viewModel.onIntent(CreateTaskIntent.Submit)
        runCurrent()

        job.cancel()
        assertEquals(listOf(CreateTaskEvent.ShowError(UiText.StringResource(R.string.create_task_failed))), events)
    }

    private fun fillValidForm() {
        viewModel.onIntent(CreateTaskIntent.DescriptionChanged("Box of books"))
        viewModel.onIntent(CreateTaskIntent.FromChanged("Warehouse A"))
        viewModel.onIntent(CreateTaskIntent.ToChanged("Warehouse B"))
    }
}
