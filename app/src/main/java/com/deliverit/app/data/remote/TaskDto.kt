package com.deliverit.app.data.remote

import com.deliverit.app.model.DeliveryStatus
import com.deliverit.app.model.DeliveryTask
import com.deliverit.app.model.StatusHistoryEntry

data class TaskDto(
    val id: String,
    val itemDescription: String,
    val fromLocation: String,
    val toLocation: String,
    val status: String,
    val createdAt: Long,
    val statusHistory: List<StatusHistoryEntryDto>
)

data class StatusHistoryEntryDto(
    val status: String,
    val timestamp: Long
)

fun TaskDto.toDomain() = DeliveryTask(
    id = id,
    itemDescription = itemDescription,
    fromLocation = fromLocation,
    toLocation = toLocation,
    status = DeliveryStatus.valueOf(status),
    createdAt = createdAt,
    statusHistory = statusHistory.map { it.toDomain() }
)

fun StatusHistoryEntryDto.toDomain() = StatusHistoryEntry(
    status = DeliveryStatus.valueOf(status),
    timestamp = timestamp
)
