package com.deliverit.app.data

import com.deliverit.app.data.local.LocalTaskDataSource
import com.deliverit.app.data.remote.RemoteTaskDataSource
import com.deliverit.app.model.DeliveryStatus
import com.deliverit.app.model.DeliveryTask
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultTaskRepository @Inject constructor(
    private val remote: RemoteTaskDataSource,
    private val local: LocalTaskDataSource
) : TaskRepository {

    override fun observeTasks(): Flow<List<DeliveryTask>> = local.observeTasks()

    override fun observeTask(id: String): Flow<DeliveryTask?> = local.observeTask(id)

    override suspend fun createTask(
        itemDescription: String,
        fromLocation: String,
        toLocation: String
    ) = runCatching {
        remote.createTask(itemDescription, fromLocation, toLocation).also { local.save(it) }
    }

    override suspend fun updateTaskStatus(taskId: String, status: DeliveryStatus) = runCatching {
        local.save(remote.updateStatus(taskId, status))
    }

    override suspend fun refreshTasks() = runCatching {
        local.replaceAll(remote.fetchTasks())
    }
}
