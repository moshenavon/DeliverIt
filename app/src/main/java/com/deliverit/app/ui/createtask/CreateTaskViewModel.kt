package com.deliverit.app.ui.createtask

import androidx.lifecycle.viewModelScope
import com.deliverit.app.R
import com.deliverit.app.data.TaskRepository
import com.deliverit.app.ui.common.MviViewModel
import com.deliverit.app.ui.common.UiText
import com.deliverit.app.ui.common.toUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : MviViewModel<CreateTaskState, CreateTaskIntent, CreateTaskEvent>(
    CreateTaskState()
) {

    override fun onIntent(intent: CreateTaskIntent) {
        when (intent) {
            is CreateTaskIntent.DescriptionChanged ->
                setState { copy(itemDescription = intent.value, descriptionError = null) }

            is CreateTaskIntent.FromChanged ->
                setState { copy(fromLocation = intent.value, fromError = null) }

            is CreateTaskIntent.ToChanged ->
                setState { copy(toLocation = intent.value, toError = null) }

            is CreateTaskIntent.Submit -> submit()
        }
    }

    private fun submit() {
        val state = currentState
        val descriptionError =
            if (state.itemDescription.isBlank()) UiText.StringResource(R.string.create_task_description_required) else null
        val fromError =
            if (state.fromLocation.isBlank()) UiText.StringResource(R.string.create_task_pickup_required) else null
        val toError =
            if (state.toLocation.isBlank()) UiText.StringResource(R.string.create_task_dropoff_required) else null

        if (descriptionError != null || fromError != null || toError != null) {
            setState {
                copy(
                    descriptionError = descriptionError,
                    fromError = fromError,
                    toError = toError
                )
            }
            return
        }

        viewModelScope.launch {
            setState { copy(isSubmitting = true) }
            repository.createTask(
                itemDescription = state.itemDescription.trim(),
                fromLocation = state.fromLocation.trim(),
                toLocation = state.toLocation.trim()
            ).onSuccess {
                sendEvent(CreateTaskEvent.NavigateBack)
            }.onFailure { e ->
                sendEvent(CreateTaskEvent.ShowError(e.toUiText(R.string.create_task_failed)))
            }
            setState { copy(isSubmitting = false) }
        }
    }
}
