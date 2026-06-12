package com.deliverit.app.data.remote

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import retrofit2.HttpException

data class ErrorResponseDto(
    @SerializedName("error") val error: String? = null,
    @SerializedName("message") val message: String? = null
)

fun HttpException.serverMessage(): String? =
    response()?.errorBody()?.string()?.let { body ->
        runCatching { Gson().fromJson(body, ErrorResponseDto::class.java) }
            .getOrNull()
            ?.let { it.error ?: it.message }
    }
