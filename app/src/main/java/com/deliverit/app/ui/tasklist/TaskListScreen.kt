package com.deliverit.app.ui.tasklist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deliverit.app.ui.tasklist.components.CreateTaskFab
import com.deliverit.app.ui.tasklist.components.TaskListContent
import com.deliverit.app.ui.tasklist.components.TaskListTopBar

@Composable
fun TaskListScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
    viewModel: TaskListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is TaskListEvent.NavigateToDetail -> onNavigateToDetail(event.id)
                is TaskListEvent.NavigateToCreate -> onNavigateToCreate()
                is TaskListEvent.ShowError -> snackbarHostState.showSnackbar(event.message.asString(context))
            }
        }
    }

    Scaffold(
        topBar = { TaskListTopBar() },
        floatingActionButton = {
            CreateTaskFab(onClick = { viewModel.onIntent(TaskListIntent.CreateTask) })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        TaskListContent(
            tasks = state.tasks,
            isLoading = state.isLoading,
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.onIntent(TaskListIntent.Refresh) },
            onTaskClick = { task -> viewModel.onIntent(TaskListIntent.SelectTask(task.id)) },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}
