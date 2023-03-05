package com.hanna.textrecognition.di

import com.hanna.textrecognition.data.core.AppDispatchers
import com.hanna.textrecognition.data.core.AppDispatchersImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideAppDispatchers(): AppDispatchers {
        return AppDispatchersImpl()
    }
}