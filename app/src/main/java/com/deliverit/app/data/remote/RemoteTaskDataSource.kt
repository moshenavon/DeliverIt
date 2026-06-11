package com.deliverit.app.data.remote

import com.deliverit.app.model.DeliveryStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteTaskDataSource @Inject constructor(
    private val api: TaskApi
) {

    suspend fun fetchTasks(): List<TaskDto> = api.getTasks()

    suspend fun createTask(
        itemDescription: String,
        fromLocation: String,
        toLocation: String
    ): TaskDto =
        api.createTask(CreateTaskRequest(itemDescription, fromLocation, toLocation))

    suspend fun updateStatus(taskId: String, status: DeliveryStatus): TaskDto =
        api.updateStatus(taskId, UpdateStatusRequest(status.name))
}
