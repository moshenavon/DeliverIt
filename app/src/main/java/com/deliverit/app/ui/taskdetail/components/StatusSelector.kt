package com.deliverit.app.ui.taskdetail.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.deliverit.app.model.DeliveryStatus
import com.deliverit.app.ui.common.label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusSelector(
    currentStatus: DeliveryStatus,
    onStatusSelected: (DeliveryStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DeliveryStatus.entries.forEach { status ->
            FilterChip(
                selected = status == currentStatus,
                onClick = { onStatusSelected(status) },
                label = { Text(status.label()) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}
