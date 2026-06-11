package com.deliverit.app.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.deliverit.app.R
import com.deliverit.app.model.DeliveryStatus
import com.deliverit.app.ui.theme.StatusAssigned
import com.deliverit.app.ui.theme.StatusDelivered
import com.deliverit.app.ui.theme.StatusFailed
import com.deliverit.app.ui.theme.StatusInTransit
import com.deliverit.app.ui.theme.StatusPending

@Composable
fun DeliveryStatus.label(): String = when (this) {
    DeliveryStatus.PENDING -> stringResource(R.string.status_pending)
    DeliveryStatus.ASSIGNED -> stringResource(R.string.status_assigned)
    DeliveryStatus.IN_TRANSIT -> stringResource(R.string.status_in_transit)
    DeliveryStatus.DELIVERED -> stringResource(R.string.status_delivered)
    DeliveryStatus.FAILED -> stringResource(R.string.status_failed)
}

fun DeliveryStatus.color() = when (this) {
    DeliveryStatus.PENDING -> StatusPending
    DeliveryStatus.ASSIGNED -> StatusAssigned
    DeliveryStatus.IN_TRANSIT -> StatusInTransit
    DeliveryStatus.DELIVERED -> StatusDelivered
    DeliveryStatus.FAILED -> StatusFailed
}

@Composable
fun StatusBadge(status: DeliveryStatus, modifier: Modifier = Modifier) {
    val color = status.color()
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.15f),
        contentColor = color,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status.label(),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
