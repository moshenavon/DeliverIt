package com.deliverit.app.ui.common

import com.deliverit.app.R
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class ThrowableExtTest {

    private val fallback = R.string.task_list_refresh_failed

    private fun httpException(body: String) = HttpException(
        Response.error<Any>(500, body.toResponseBody("application/json".toMediaType()))
    )

    @Test
    fun `http exception with an error field uses the server message`() {
        val exception = httpException("""{"error":"Task not found"}""")

        assertEquals(UiText.DynamicString("Task not found"), exception.toUiText(fallback))
    }

    @Test
    fun `http exception with a message field uses the server message`() {
        val exception = httpException("""{"message":"Invalid status transition"}""")

        assertEquals(UiText.DynamicString("Invalid status transition"), exception.toUiText(fallback))
    }

    @Test
    fun `http exception prefers the error field over the message field`() {
        val exception = httpException("""{"error":"from error","message":"from message"}""")

        assertEquals(UiText.DynamicString("from error"), exception.toUiText(fallback))
    }

    @Test
    fun `http exception with an unparseable body falls back to the exception message`() {
        val exception = httpException("<html>Bad Gateway</html>")

        assertEquals(UiText.DynamicString(exception.message!!), exception.toUiText(fallback))
    }

    @Test
    fun `http exception with an empty body falls back to the exception message`() {
        val exception = httpException("")

        assertEquals(UiText.DynamicString(exception.message!!), exception.toUiText(fallback))
    }

    @Test
    fun `plain exception uses its own message`() {
        val exception = RuntimeException("connection refused")

        assertEquals(UiText.DynamicString("connection refused"), exception.toUiText(fallback))
    }

    @Test
    fun `exception without a message falls back to the string resource`() {
        val exception = RuntimeException()

        assertEquals(UiText.StringResource(fallback), exception.toUiText(fallback))
    }
}
