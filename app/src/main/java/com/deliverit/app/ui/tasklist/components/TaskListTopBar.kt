package com.deliverit.app.ui.tasklist.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.deliverit.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListTopBar() {
    TopAppBar(title = { Text(stringResource(R.string.task_list_title)) })
}
