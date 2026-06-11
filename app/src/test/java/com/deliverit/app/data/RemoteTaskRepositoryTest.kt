package com.deliverit.app.data

import com.deliverit.app.data.remote.CreateTaskRequest
import com.deliverit.app.data.remote.StatusHistoryEntryDto
import com.deliverit.app.data.remote.TaskApi
import com.deliverit.app.data.remote.TaskDto
import com.deliverit.app.data.remote.UpdateStatusRequest
import com.deliverit.app.model.DeliveryStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

class RemoteTaskRepositoryTest {

    private lateinit var api: TaskApi
    private lateinit var repository: RemoteTaskRepository

    @Before
    fun setUp() {
        api = mockk()
        repository = RemoteTaskRepository(api)
    }

    private fun sampleTaskDto(
        id: String = "task-1",
        itemDescription: String = "Box of books",
        fromLocation: String = "Warehouse A",
        toLocation: String = "Warehouse B",
        status: String = "PENDING",
        createdAt: Long = 0L,
        statusHistory: List<StatusHistoryEntryDto> = listOf(StatusHistoryEntryDto(status, createdAt))
    ) = TaskDto(
        id = id,
        itemDescription = itemDescription,
        fromLocation = fromLocation,
        toLocation = toLocation,
        status = status,
        createdAt = createdAt,
        statusHistory = statusHistory
    )

    @Test
    fun `createTask returns the task mapped to domain on success`() = runTest {
        val dto = sampleTaskDto()
        coEvery { api.createTask(any()) } returns dto

        val result = repository.createTask("Box of books", "Warehouse A", "Warehouse B")

        assertTrue(result.isSuccess)
        val task = result.getOrThrow()
        assertEquals(dto.id, task.id)
        assertEquals(dto.itemDescription, task.itemDescription)
        assertEquals(dto.fromLocation, task.fromLocation)
        assertEquals(dto.toLocation, task.toLocation)
        assertEquals(DeliveryStatus.PENDING, task.status)
        assertEquals(listOf(DeliveryStatus.PENDING), task.statusHistory.map { it.status })
        coVerify {
            api.createTask(CreateTaskRequest("Box of books", "Warehouse A", "Warehouse B"))
        }
    }

    @Test
    fun `createTask adds the new task to the observed tasks`() = runTest {
        coEvery { api.createTask(any()) } returns sampleTaskDto()

        val created = repository.createTask("Box of books", "A", "B").getOrThrow()

        assertEquals(listOf(created), repository.observeTasks().first())
    }

    @Test
    fun `createTask returns failure and keeps tasks unchanged when the api throws`() = runTest {
        coEvery { api.createTask(any()) } throws RuntimeException("network error")

        val result = repository.createTask("Box of books", "A", "B")

        assertTrue(result.isFailure)
        assertTrue(repository.observeTasks().first().isEmpty())
    }

    @Test
    fun `refreshTasks replaces the observed tasks with the api response`() = runTest {
        coEvery { api.getTasks() } returns listOf(
            sampleTaskDto(id = "task-1"),
            sampleTaskDto(id = "task-2", status = "ASSIGNED")
        )

        val result = repository.refreshTasks()

        assertTrue(result.isSuccess)
        val tasks = repository.observeTasks().first()
        assertEquals(listOf("task-1", "task-2"), tasks.map { it.id }.sorted())
    }

    @Test
    fun `refreshTasks returns failure and keeps tasks unchanged when the api throws`() = runTest {
        coEvery { api.createTask(any()) } returns sampleTaskDto()
        val created = repository.createTask("Box of books", "A", "B").getOrThrow()
        coEvery { api.getTasks() } throws RuntimeException("network error")

        val result = repository.refreshTasks()

        assertTrue(result.isFailure)
        assertEquals(listOf(created), repository.observeTasks().first())
    }

    @Test
    fun `updateTaskStatus replaces the matching task with the api response`() = runTest {
        coEvery { api.getTasks() } returns listOf(
            sampleTaskDto(id = "task-1"),
            sampleTaskDto(id = "task-2", createdAt = 1L)
        )
        repository.refreshTasks()
        coEvery { api.updateStatus("task-1", any()) } returns
            sampleTaskDto(id = "task-1", status = "ASSIGNED")

        val result = repository.updateTaskStatus("task-1", DeliveryStatus.ASSIGNED)

        assertTrue(result.isSuccess)
        val updated = repository.observeTask("task-1").first()
        assertEquals(DeliveryStatus.ASSIGNED, updated?.status)
        val untouched = repository.observeTask("task-2").first()
        assertEquals(DeliveryStatus.PENDING, untouched?.status)
        coVerify { api.updateStatus("task-1", UpdateStatusRequest("ASSIGNED")) }
    }

    @Test
    fun `updateTaskStatus returns failure and keeps tasks unchanged when the api throws`() = runTest {
        coEvery { api.createTask(any()) } returns sampleTaskDto()
        val created = repository.createTask("Box of books", "A", "B").getOrThrow()
        coEvery { api.updateStatus(any(), any()) } throws RuntimeException("network error")

        val result = repository.updateTaskStatus(created.id, DeliveryStatus.ASSIGNED)

        assertTrue(result.isFailure)
        assertEquals(DeliveryStatus.PENDING, repository.observeTask(created.id).first()?.status)
    }

    @Test
    fun `observeTasks returns tasks sorted by createdAt descending`() = runTest {
        coEvery { api.getTasks() } returns listOf(
            sampleTaskDto(id = "old", createdAt = 1L),
            sampleTaskDto(id = "new", createdAt = 2L)
        )
        repository.refreshTasks()

        val tasks = repository.observeTasks().first()

        assertEquals(listOf("new", "old"), tasks.map { it.id })
    }

    @Test
    fun `observeTask returns null when no task matches the id`() = runTest {
        coEvery { api.createTask(any()) } returns sampleTaskDto()
        repository.createTask("Box of books", "A", "B")

        assertNull(repository.observeTask("unknown-id").first())
    }
}
