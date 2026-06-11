package com.deliverit.app.ui.createtask

import com.deliverit.app.ui.common.UiText

data class CreateTaskState(
    val itemDescription: String = "",
    val fromLocation: String = "",
    val toLocation: String = "",
    val descriptionError: UiText? = null,
    val fromError: UiText? = null,
    val toError: UiText? = null,
    val isSubmitting: Boolean = false
)

sealed interface CreateTaskIntent {
    data class DescriptionChanged(val value: String) : CreateTaskIntent
    data class FromChanged(val value: String) : CreateTaskIntent
    data class ToChanged(val value: String) : CreateTaskIntent
    data object Submit : CreateTaskIntent
}

sealed interface CreateTaskEvent {
    data object NavigateBack : CreateTaskEvent
    data class ShowError(val message: UiText) : CreateTaskEvent
}
