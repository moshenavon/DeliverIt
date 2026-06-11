package com.deliverit.app.data

import com.deliverit.app.model.DeliveryStatus
import com.deliverit.app.model.DeliveryTask
import com.deliverit.app.model.StatusHistoryEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory [TaskRepository] that stands in for the real Room+Retrofit repository
 * until implement...
 */
@Singleton
class FakeTaskRepository @Inject constructor() : TaskRepository {

    private val tasks = MutableStateFlow<List<DeliveryTask>>(emptyList())

    override fun observeTasks(): Flow<List<DeliveryTask>> =
        tasks.asStateFlow().map { list -> list.sortedByDescending { it.createdAt } }

    override fun observeTask(id: String): Flow<DeliveryTask?> =
        observeTasks().map { list -> list.firstOrNull { it.id == id } }

    override suspend fun createTask(
        itemDescription: String,
        fromLocation: String,
        toLocation: String
    ): Result<DeliveryTask> {
        val now = System.currentTimeMillis()
        val task = DeliveryTask(
            id = UUID.randomUUID().toString(),
            itemDescription = itemDescription,
            fromLocation = fromLocation,
            toLocation = toLocation,
            status = DeliveryStatus.PENDING,
            createdAt = now,
            statusHistory = listOf(StatusHistoryEntry(DeliveryStatus.PENDING, now))
        )
        tasks.update { it + task }
        return Result.success(task)
    }

    override suspend fun updateTaskStatus(taskId: String, status: DeliveryStatus): Result<Unit> {
        tasks.update { current ->
            current.map { task ->
                if (task.id == taskId) {
                    task.copy(
                        status = status,
                        statusHistory = task.statusHistory + StatusHistoryEntry(
                            status = status,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                } else {
                    task
                }
            }
        }
        return Result.success(Unit)
    }

    override suspend fun refreshTasks(): Result<Unit> = Result.success(Unit)
}
