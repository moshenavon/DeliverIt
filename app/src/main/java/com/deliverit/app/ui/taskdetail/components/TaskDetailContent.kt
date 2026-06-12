package com.deliverit.app.ui.taskdetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.deliverit.app.R
import com.deliverit.app.model.DeliveryStatus
import com.deliverit.app.model.DeliveryTask

@Composable
fun TaskDetailContent(
    task: DeliveryTask,
    onStatusSelected: (DeliveryStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TaskSummaryCard(task = task)

        if (task.status == DeliveryStatus.FAILED) {
            FailedDeliveryBanner()
        }

        SectionTitle(text = stringResource(R.string.task_detail_update_status))
        StatusSelector(
            currentStatus = task.status,
            onStatusSelected = onStatusSelected
        )

        SectionTitle(text = stringResource(R.string.task_detail_status_timeline))
        StatusTimeline(entries = task.statusHistory)
    }
}
