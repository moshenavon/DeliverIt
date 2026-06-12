package com.deliverit.app.ui.tasklist

import androidx.lifecycle.viewModelScope
import com.deliverit.app.R
import com.deliverit.app.data.TaskRepository
import com.deliverit.app.ui.common.MviViewModel
import com.deliverit.app.ui.common.toUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val repository: TaskRepository
) : MviViewModel<TaskListState, TaskListIntent, TaskListEvent>(
    TaskListState(isLoading = true)
) {

    init {
        repository.observeTasks()
            .onStart { setState { copy(isLoading = false) } }
            .onEach { tasks -> setState { copy(tasks = tasks) } }
            .launchIn(viewModelScope)
        refresh()
    }

    override fun onIntent(intent: TaskListIntent) {
        when (intent) {
            is TaskListIntent.Refresh -> refresh()
            is TaskListIntent.SelectTask -> sendEvent(TaskListEvent.NavigateToDetail(intent.id))
            is TaskListIntent.CreateTask -> sendEvent(TaskListEvent.NavigateToCreate)
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            setState { copy(isRefreshing = true) }
            repository.refreshTasks()
                .onFailure { e ->
                    sendEvent(TaskListEvent.ShowError(e.toUiText(R.string.task_list_refresh_failed)))
                }
            setState { copy(isRefreshing = false, isLoading = false) }
        }
    }
}
