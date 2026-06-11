package com.deliverit.app.model

data class DeliveryTask(
    val id: String,
    val itemDescription: String,
    val fromLocation: String,
    val toLocation: String,
    val status: DeliveryStatus,
    val createdAt: Long,
    val statusHistory: List<StatusHistoryEntry>
)
