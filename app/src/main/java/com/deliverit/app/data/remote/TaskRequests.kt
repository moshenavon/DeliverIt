package com.deliverit.app.data.remote

import com.google.gson.annotations.SerializedName

data class CreateTaskRequest(
    @SerializedName("itemDescription") val itemDescription: String,
    @SerializedName("fromLocation") val fromLocation: String,
    @SerializedName("toLocation") val toLocation: String
)

data class UpdateStatusRequest(
    @SerializedName("status") val status: String
)
