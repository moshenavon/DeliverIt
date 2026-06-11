package com.deliverit.app.data

import com.deliverit.app.model.DeliveryStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

class FakeTaskRepositoryTest {

    private lateinit var repository: FakeTaskRepository

    @Before
    fun setUp() {
        repository = FakeTaskRepository()
    }

    @Test
    fun `createTask returns a pending task with an initial status history entry`() = runTest {
        val result = repository.createTask("Box of books", "Warehouse A", "Warehouse B")

        assertTrue(result.isSuccess)
        val task = result.getOrThrow()
        assertEquals("Box of books", task.itemDescription)
        assertEquals("Warehouse A", task.fromLocation)
        assertEquals("Warehouse B", task.toLocation)
        assertEquals(DeliveryStatus.PENDING, task.status)
        assertEquals(listOf(DeliveryStatus.PENDING), task.statusHistory.map { it.status })
    }

    @Test
    fun `observeTasks returns tasks sorted by createdAt descending`() = runTest {
        repository.createTask("First", "A", "B")
        repository.createTask("Second", "C", "D")

        val tasks = repository.observeTasks().first()

        assertEquals(2, tasks.size)
        assertEquals(tasks.sortedByDescending { it.createdAt }, tasks)
    }

    @Test
    fun `observeTask returns null when no task matches the id`() = runTest {
        repository.createTask("Box of books", "A", "B")

        val task = repository.observeTask("unknown-id").first()

        assertNull(task)
    }

    @Test
    fun `observeTask returns the task with the matching id`() = runTest {
        val created = repository.createTask("Box of books", "A", "B").getOrThrow()

        val task = repository.observeTask(created.id).first()

        assertEquals(created, task)
    }

    @Test
    fun `updateTaskStatus updates the status and appends a history entry`() = runTest {
        val created = repository.createTask("Box of books", "A", "B").getOrThrow()

        val result = repository.updateTaskStatus(created.id, DeliveryStatus.ASSIGNED)

        assertTrue(result.isSuccess)
        val updated = repository.observeTask(created.id).first()
        assertEquals(DeliveryStatus.ASSIGNED, updated?.status)
        assertEquals(
            listOf(DeliveryStatus.PENDING, DeliveryStatus.ASSIGNED),
            updated?.statusHistory?.map { it.status }
        )
    }

    @Test
    fun `updateTaskStatus to FAILED updates the status`() = runTest {
        val created = repository.createTask("Box of books", "A", "B").getOrThrow()

        repository.updateTaskStatus(created.id, DeliveryStatus.FAILED)

        val updated = repository.observeTask(created.id).first()
        assertEquals(DeliveryStatus.FAILED, updated?.status)
    }

    @Test
    fun `updateTaskStatus with an unknown id leaves existing tasks unchanged`() = runTest {
        val created = repository.createTask("Box of books", "A", "B").getOrThrow()

        val result = repository.updateTaskStatus("unknown-id", DeliveryStatus.ASSIGNED)

        assertTrue(result.isSuccess)
        assertEquals(listOf(created), repository.observeTasks().first())
    }

    @Test
    fun `refreshTasks always returns success`() = runTest {
        assertTrue(repository.refreshTasks().isSuccess)
    }
}
