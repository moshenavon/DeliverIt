package com.deliverit.app.ui.createtask.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.deliverit.app.R
import com.deliverit.app.ui.createtask.CreateTaskIntent
import com.deliverit.app.ui.createtask.CreateTaskState

@Composable
fun CreateTaskForm(
    state: CreateTaskState,
    onIntent: (CreateTaskIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TaskFormTextField(
            value = state.itemDescription,
            onValueChange = { onIntent(CreateTaskIntent.DescriptionChanged(it)) },
            label = stringResource(R.string.create_task_item_description_label),
            error = state.descriptionError
        )
        TaskFormTextField(
            value = state.fromLocation,
            onValueChange = { onIntent(CreateTaskIntent.FromChanged(it)) },
            label = stringResource(R.string.create_task_from_location_label),
            error = state.fromError
        )
        TaskFormTextField(
            value = state.toLocation,
            onValueChange = { onIntent(CreateTaskIntent.ToChanged(it)) },
            label = stringResource(R.string.create_task_to_location_label),
            error = state.toError
        )
        SubmitTaskButton(
            isSubmitting = state.isSubmitting,
            onClick = { onIntent(CreateTaskIntent.Submit) }
        )
    }
}
