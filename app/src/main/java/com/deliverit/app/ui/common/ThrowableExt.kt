package com.deliverit.app.ui.common

import androidx.annotation.StringRes
import com.deliverit.app.data.remote.serverMessage
import retrofit2.HttpException

/**
 * Maps a failure to a user-facing message: the server's error message when available,
 * then the exception message, then the given fallback resource.
 */
fun Throwable.toUiText(@StringRes fallbackResId: Int): UiText {
    val serverMessage = (this as? HttpException)?.serverMessage()
    return (serverMessage ?: message)?.let { UiText.DynamicString(it) }
        ?: UiText.StringResource(fallbackResId)
}
