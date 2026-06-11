package com.deliverit.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.deliverit.app.model.DeliveryStatus
import com.deliverit.app.model.DeliveryTask
import com.deliverit.app.model.StatusHistoryEntry
import com.google.gson.annotations.SerializedName

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
    @SerializedName("status") val status: String,
    @SerializedName("timestamp") val timestamp: Long
)

fun TaskEntity.toDomain() = DeliveryTask(
    id = id,
    itemDescription = itemDescription,
    fromLocation = fromLocation,
    toLocation = toLocation,
    status = DeliveryStatus.fromName(status),
    createdAt = createdAt,
    statusHistory = statusHistory.map {
        StatusHistoryEntry(DeliveryStatus.fromName(it.status), it.timestamp)
    }
)
