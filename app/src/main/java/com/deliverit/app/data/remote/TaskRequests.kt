package com.deliverit.app.data.remote

data class CreateTaskRequest(
    val itemDescription: String,
    val fromLocation: String,
    val toLocation: String
)

data class UpdateStatusRequest(
    val status: String
)
