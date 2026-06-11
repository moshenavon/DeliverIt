package com.deliverit.app.ui.taskdetail

import com.deliverit.app.model.DeliveryStatus
import com.deliverit.app.model.DeliveryTask
import com.deliverit.app.ui.common.UiText

data class TaskDetailState(
    val task: DeliveryTask? = null,
    val isLoading: Boolean = true
)

sealed interface TaskDetailIntent {
    data object Refresh : TaskDetailIntent
    data class ChangeStatus(val status: DeliveryStatus) : TaskDetailIntent
}

sealed interface TaskDetailEvent {
    data class ShowError(val message: UiText) : TaskDetailEvent
}
