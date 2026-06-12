package com.deliverit.app.ui.createtask.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.deliverit.app.ui.common.UiText

@Composable
fun TaskFormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: UiText?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = { error?.let { Text(it.asString(context)) } },
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}
