package com.deliverit.app.data.remote

import com.deliverit.app.data.local.StatusHistoryEntryColumn
import com.deliverit.app.data.local.TaskEntity
import com.deliverit.app.model.DeliveryStatus
import com.google.gson.annotations.SerializedName

data class TaskDto(
    @SerializedName("id") val id: String,
    @SerializedName("itemDescription") val itemDescription: String,
    @SerializedName("fromLocation") val fromLocation: String,
    @SerializedName("toLocation") val toLocation: String,
    @SerializedName("status") val status: String,
    @SerializedName("createdAt") val createdAt: Long,
    @SerializedName("statusHistory") val statusHistory: List<StatusHistoryEntryDto>
)

data class StatusHistoryEntryDto(
    @SerializedName("status") val status: String,
    @SerializedName("timestamp") val timestamp: Long
)

fun TaskDto.toEntity() = TaskEntity(
    id = id,
    itemDescription = itemDescription,
    fromLocation = fromLocation,
    toLocation = toLocation,
    status = DeliveryStatus.fromName(status).name,
    createdAt = createdAt,
    statusHistory = statusHistory.map {
        StatusHistoryEntryColumn(DeliveryStatus.fromName(it.status).name, it.timestamp)
    }
)
