package com.deliverit.app.ui.taskdetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.deliverit.app.R
import com.deliverit.app.model.DeliveryTask
import com.deliverit.app.ui.common.StatusBadge

@Composable
fun TaskSummaryCard(task: DeliveryTask, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.route_format, task.fromLocation, task.toLocation),
                style = MaterialTheme.typography.titleMedium
            )
            StatusBadge(status = task.status)
        }
    }
}
