package com.github.bumblebee202111.doubean.data

import android.content.Context
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage.Companion.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataModule {

    @Singleton
    @Provides
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
        }

    @Singleton
    @Provides
    fun providePreferenceStorage(
        @ApplicationContext context: Context,
        json: Json,
    ): PreferenceStorage =
        PreferenceStorage(context.dataStore, json)
}