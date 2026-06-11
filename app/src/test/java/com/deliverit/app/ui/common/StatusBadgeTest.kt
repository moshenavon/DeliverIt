package com.deliverit.app.ui.common

import com.deliverit.app.model.DeliveryStatus
import com.deliverit.app.ui.theme.StatusAssigned
import com.deliverit.app.ui.theme.StatusDelivered
import com.deliverit.app.ui.theme.StatusFailed
import com.deliverit.app.ui.theme.StatusInTransit
import com.deliverit.app.ui.theme.StatusPending
import org.junit.Test
import org.junit.Assert.assertEquals

class StatusBadgeTest {

    @Test
    fun `each delivery status maps to its theme color`() {
        assertEquals(StatusPending, DeliveryStatus.PENDING.color())
        assertEquals(StatusAssigned, DeliveryStatus.ASSIGNED.color())
        assertEquals(StatusInTransit, DeliveryStatus.IN_TRANSIT.color())
        assertEquals(StatusDelivered, DeliveryStatus.DELIVERED.color())
        assertEquals(StatusFailed, DeliveryStatus.FAILED.color())
    }
}
