package com.deliverit.app.di

import android.content.Context
import androidx.room.Room
import com.deliverit.app.data.local.DeliverItDatabase
import com.deliverit.app.data.local.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DB_NAME = "deliverit"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DeliverItDatabase =
        Room.databaseBuilder(context, DeliverItDatabase::class.java, DB_NAME)
            .build()

    @Provides
    fun provideTaskDao(database: DeliverItDatabase): TaskDao = database.taskDao()
}
