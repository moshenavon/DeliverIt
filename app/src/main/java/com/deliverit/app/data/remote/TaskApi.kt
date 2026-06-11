package com.deliverit.app.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface TaskApi {

    @GET("tasks")
    suspend fun getTasks(): List<TaskDto>

    @POST("tasks")
    suspend fun createTask(@Body request: CreateTaskRequest): TaskDto

    @PATCH("tasks/{id}/status")
    suspend fun updateStatus(@Path("id") id: String, @Body request: UpdateStatusRequest): TaskDto
}
