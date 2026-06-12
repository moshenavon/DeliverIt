package com.deliverit.app.ui.createtask

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
import com.deliverit.app.ui.createtask.components.CreateTaskForm
import com.deliverit.app.ui.createtask.components.CreateTaskTopBar

@Composable
fun CreateTaskScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateTaskViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                CreateTaskEvent.NavigateBack -> onNavigateBack()
                is CreateTaskEvent.ShowError -> snackbarHostState.showSnackbar(event.message.asString(context))
            }
        }
    }

    Scaffold(
        topBar = { CreateTaskTopBar(onNavigateBack = onNavigateBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        CreateTaskForm(
            state = state,
            onIntent = viewModel::onIntent,
            modifier = Modifier.padding(padding)
        )
    }
}
