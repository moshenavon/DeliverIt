package com.deliverit.app.ui.taskdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.deliverit.app.R
import com.deliverit.app.data.TaskRepository
import com.deliverit.app.model.DeliveryStatus
import com.deliverit.app.ui.common.MviViewModel
import com.deliverit.app.ui.common.toUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val repository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : MviViewModel<TaskDetailState, TaskDetailIntent, TaskDetailEvent>(
    TaskDetailState()
) {

    private val taskId: String = checkNotNull(savedStateHandle["taskId"])

    init {
        repository.observeTask(taskId)
            .onEach { task -> setState { copy(task = task, isLoading = false) } }
            .launchIn(viewModelScope)
    }

    override fun onIntent(intent: TaskDetailIntent) {
        when (intent) {
            TaskDetailIntent.Refresh -> refresh()
            is TaskDetailIntent.ChangeStatus -> changeStatus(intent.status)
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            repository.refreshTasks().onFailure { e ->
                sendEvent(TaskDetailEvent.ShowError(e.toUiText(R.string.task_detail_refresh_failed)))
            }
        }
    }

    private fun changeStatus(status: DeliveryStatus) {
        if (status == currentState.task?.status) return

        viewModelScope.launch {
            repository.updateTaskStatus(taskId, status).onFailure { e ->
                sendEvent(TaskDetailEvent.ShowError(e.toUiText(R.string.task_detail_update_status_failed)))
            }
        }
    }
}
