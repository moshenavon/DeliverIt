package com.deliverit.app.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TaskConverters {

    private val gson = Gson()
    private val historyType = object : TypeToken<List<StatusHistoryEntryColumn>>() {}.type

    @TypeConverter
    fun fromStatusHistory(history: List<StatusHistoryEntryColumn>): String = gson.toJson(history)

    @TypeConverter
    fun toStatusHistory(json: String): List<StatusHistoryEntryColumn> = gson.fromJson(json, historyType)
}
