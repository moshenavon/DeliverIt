package com.deliverit.app.data

import com.deliverit.app.data.remote.CreateTaskRequest
import com.deliverit.app.data.remote.TaskApi
import com.deliverit.app.data.remote.UpdateStatusRequest
import com.deliverit.app.data.remote.toDomain
import com.deliverit.app.model.DeliveryStatus
import com.deliverit.app.model.DeliveryTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteTaskRepository @Inject constructor(
    private val api: TaskApi
) : TaskRepository {

    private val tasks = MutableStateFlow<List<DeliveryTask>>(emptyList())

    override fun observeTasks(): Flow<List<DeliveryTask>> =
        tasks.asStateFlow().map { list -> list.sortedByDescending { it.createdAt } }

    override fun observeTask(id: String): Flow<DeliveryTask?> =
        observeTasks().map { list -> list.firstOrNull { it.id == id } }

    override suspend fun createTask(
        itemDescription: String,
        fromLocation: String,
        toLocation: String
    ) = runCatching {
        val task = api.createTask(CreateTaskRequest(itemDescription, fromLocation, toLocation)).toDomain()
        tasks.update { it + task }
        task
    }

    override suspend fun updateTaskStatus(taskId: String, status: DeliveryStatus) = runCatching {
        val updated = api.updateStatus(taskId, UpdateStatusRequest(status.name)).toDomain()
        tasks.update { current -> current.map { if (it.id == taskId) updated else it } }
    }

    override suspend fun refreshTasks() = runCatching {
        tasks.value = api.getTasks().map { it.toDomain() }
    }
}
