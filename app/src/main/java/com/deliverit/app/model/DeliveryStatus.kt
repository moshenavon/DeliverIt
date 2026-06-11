package com.deliverit.app.model

enum class DeliveryStatus {
    PENDING,
    ASSIGNED,
    IN_TRANSIT,
    DELIVERED,
    FAILED;

    companion object {
        fun fromName(name: String?): DeliveryStatus =
            entries.firstOrNull { it.name == name } ?: PENDING
    }
}
