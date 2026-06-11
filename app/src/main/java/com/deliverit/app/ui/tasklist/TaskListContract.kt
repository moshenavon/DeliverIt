package com.deliverit.app.ui.tasklist

import com.deliverit.app.model.DeliveryTask
import com.deliverit.app.ui.common.UiText

data class TaskListState(
    val tasks: List<DeliveryTask> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false
)

sealed interface TaskListIntent {
    data object Refresh : TaskListIntent
    data class SelectTask(val id: String) : TaskListIntent
    data object CreateTask : TaskListIntent
}

sealed interface TaskListEvent {
    data class NavigateToDetail(val id: String) : TaskListEvent
    data object NavigateToCreate : TaskListEvent
    data class ShowError(val message: UiText) : TaskListEvent
}
