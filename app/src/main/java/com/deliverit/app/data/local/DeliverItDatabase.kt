package com.deliverit.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
@TypeConverters(TaskConverters::class)
abstract class DeliverItDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
}
