package com.deliverit.app.data

import com.deliverit.app.data.local.TaskDao
import com.deliverit.app.data.local.toDomain
import com.deliverit.app.data.remote.RemoteTaskDataSource
import com.deliverit.app.data.remote.toEntity
import com.deliverit.app.model.DeliveryStatus
import com.deliverit.app.model.DeliveryTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultTaskRepository @Inject constructor(
    private val remote: RemoteTaskDataSource,
    private val dao: TaskDao
) : TaskRepository {

    override fun observeTasks(): Flow<List<DeliveryTask>> =
        dao.observeTasks().map { list -> list.map { it.toDomain() } }

    override fun observeTask(id: String): Flow<DeliveryTask?> =
        dao.observeTask(id).map { it?.toDomain() }

    override suspend fun createTask(
        itemDescription: String,
        fromLocation: String,
        toLocation: String
    ) = runCatching {
        val entity = remote.createTask(itemDescription, fromLocation, toLocation).toEntity()
        dao.upsert(entity)
        entity.toDomain()
    }

    override suspend fun updateTaskStatus(taskId: String, status: DeliveryStatus) = runCatching {
        dao.upsert(remote.updateStatus(taskId, status).toEntity())
    }

    override suspend fun refreshTasks() = runCatching {
        dao.replaceAll(remote.fetchTasks().map { it.toEntity() })
    }
}
