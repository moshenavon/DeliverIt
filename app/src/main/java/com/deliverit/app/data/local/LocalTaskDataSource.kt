package com.deliverit.app.data.local

import com.deliverit.app.model.DeliveryTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalTaskDataSource @Inject constructor(
    private val dao: TaskDao
) {

    fun observeTasks(): Flow<List<DeliveryTask>> =
        dao.observeTasks().map { list -> list.map { it.toDomain() } }

    fun observeTask(id: String): Flow<DeliveryTask?> =
        dao.observeTask(id).map { it?.toDomain() }

    suspend fun save(task: DeliveryTask) =
        dao.upsert(task.toEntity())

    suspend fun replaceAll(tasks: List<DeliveryTask>) =
        dao.replaceAll(tasks.map { it.toEntity() })
}
