package com.deliverit.app.ui.common

import android.content.Context
import androidx.annotation.StringRes

sealed interface UiText {
    data class DynamicString(val value: String) : UiText
    data class StringResource(@StringRes val resId: Int) : UiText

    fun asString(context: Context): String = when (this) {
        is DynamicString -> value
        is StringResource -> context.getString(resId)
    }
}
