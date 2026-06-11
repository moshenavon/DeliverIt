package com.deliverit.app.ui.common

import android.content.Context
import com.deliverit.app.R
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class UiTextTest {

    @Test
    fun `DynamicString resolves to its own value regardless of context`() {
        val context = mockk<Context>()

        val result = UiText.DynamicString("Hello").asString(context)

        assertEquals("Hello", result)
    }

    @Test
    fun `StringResource resolves via context getString`() {
        val context = mockk<Context>()
        every { context.getString(R.string.create_task_failed) } returns "Failed to create task"

        val result = UiText.StringResource(R.string.create_task_failed).asString(context)

        assertEquals("Failed to create task", result)
    }
}
