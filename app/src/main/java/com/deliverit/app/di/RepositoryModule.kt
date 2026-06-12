package com.deliverit.app.di

import com.deliverit.app.data.DefaultTaskRepository
import com.deliverit.app.data.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: DefaultTaskRepository): TaskRepository
}
