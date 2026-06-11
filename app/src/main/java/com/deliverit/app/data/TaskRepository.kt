package com.deliverit.app.data

import com.deliverit.app.model.DeliveryStatus
import com.deliverit.app.model.DeliveryTask
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun observeTasks(): Flow<List<DeliveryTask>>

    fun observeTask(id: String): Flow<DeliveryTask?>

    suspend fun createTask(
        itemDescription: String,
        fromLocation: String,
        toLocation: String
    ): Result<DeliveryTask>

    suspend fun updateTaskStatus(taskId: String, status: DeliveryStatus): Result<Unit>

    suspend fun refreshTasks(): Result<Unit>
}
