package com.deliverit.app.data.remote

import com.deliverit.app.model.DeliveryStatus
import com.deliverit.app.model.DeliveryTask
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteTaskDataSource @Inject constructor(
    private val api: TaskApi
) {

    suspend fun fetchTasks(): List<DeliveryTask> =
        api.getTasks().map { it.toDomain() }

    suspend fun createTask(
        itemDescription: String,
        fromLocation: String,
        toLocation: String
    ): DeliveryTask =
        api.createTask(CreateTaskRequest(itemDescription, fromLocation, toLocation)).toDomain()

    suspend fun updateStatus(taskId: String, status: DeliveryStatus): DeliveryTask =
        api.updateStatus(taskId, UpdateStatusRequest(status.name)).toDomain()
}
