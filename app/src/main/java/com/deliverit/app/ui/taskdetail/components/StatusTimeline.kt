package com.deliverit.app.ui.taskdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.deliverit.app.model.StatusHistoryEntry
import com.deliverit.app.ui.common.color
import com.deliverit.app.ui.common.label
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatusTimeline(entries: List<StatusHistoryEntry>, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        entries.forEach { entry ->
            StatusTimelineRow(entry)
        }
    }
}

@Composable
private fun StatusTimelineRow(entry: StatusHistoryEntry) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color = entry.status.color(), shape = CircleShape)
        )
        Column {
            Text(text = entry.status.label(), style = MaterialTheme.typography.bodyLarge)
            Text(
                text = formatTimestamp(entry.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val formatter = SimpleDateFormat("MMM d, HH:mm:ss", Locale.getDefault())
    return formatter.format(Date(timestamp))
}
