package com.deliverit.app.data.remote

import com.google.gson.Gson
import retrofit2.HttpException

data class ErrorResponseDto(
    val error: String? = null,
    val message: String? = null
)

fun HttpException.serverMessage(): String? =
    response()?.errorBody()?.string()?.let { body ->
        runCatching { Gson().fromJson(body, ErrorResponseDto::class.java) }
            .getOrNull()
            ?.let { it.error ?: it.message }
    }
