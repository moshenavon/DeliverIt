package com.deliverit.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.deliverit.app.model.DeliveryStatus
import com.deliverit.app.model.DeliveryTask
import com.deliverit.app.model.StatusHistoryEntry

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val itemDescription: String,
    val fromLocation: String,
    val toLocation: String,
    val status: String,
    val createdAt: Long,
    val statusHistory: List<StatusHistoryEntryColumn>
)

data class StatusHistoryEntryColumn(
    val status: String,
    val timestamp: Long
)

fun TaskEntity.toDomain() = DeliveryTask(
    id = id,
    itemDescription = itemDescription,
    fromLocation = fromLocation,
    toLocation = toLocation,
    status = DeliveryStatus.valueOf(status),
    createdAt = createdAt,
    statusHistory = statusHistory.map { StatusHistoryEntry(DeliveryStatus.valueOf(it.status), it.timestamp) }
)

fun DeliveryTask.toEntity() = TaskEntity(
    id = id,
    itemDescription = itemDescription,
    fromLocation = fromLocation,
    toLocation = toLocation,
    status = status.name,
    createdAt = createdAt,
    statusHistory = statusHistory.map { StatusHistoryEntryColumn(it.status.name, it.timestamp) }
)
