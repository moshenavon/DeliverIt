package com.deliverit.app

import com.deliverit.app.model.DeliveryStatus
import com.deliverit.app.model.DeliveryTask
import com.deliverit.app.model.StatusHistoryEntry

fun sampleTask(
    id: String = "task-1",
    itemDescription: String = "Box of books",
    fromLocation: String = "Warehouse A",
    toLocation: String = "Warehouse B",
    status: DeliveryStatus = DeliveryStatus.PENDING,
    createdAt: Long = 0L,
    statusHistory: List<StatusHistoryEntry> = listOf(StatusHistoryEntry(status, createdAt))
) = DeliveryTask(
    id = id,
    itemDescription = itemDescription,
    fromLocation = fromLocation,
    toLocation = toLocation,
    status = status,
    createdAt = createdAt,
    statusHistory = statusHistory
)
