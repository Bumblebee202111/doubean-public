package com.github.bumblebee202111.doubean.data

import android.content.Context
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage.Companion.dataStore
import com.github.bumblebee202111.doubean.network.model.NetworkStatusCard
import com.github.bumblebee202111.doubean.network.model.NetworkSubject
import com.github.bumblebee202111.doubean.network.model.NetworkSubjectModule
import com.github.bumblebee202111.doubean.network.model.NetworkSubjectStatusCard
import com.github.bumblebee202111.doubean.network.model.NetworkSubjectWithRankAndInterest
import com.github.bumblebee202111.doubean.network.model.NetworkUnknownStatusCard
import com.github.bumblebee202111.doubean.network.model.NetworkUnknownSubject
import com.github.bumblebee202111.doubean.network.model.NetworkUnknownSubjectWithRankAndInterest
import com.github.bumblebee202111.doubean.network.model.NetworkUnsupportedSubjectStatusCard
import com.github.bumblebee202111.doubean.network.model.common.NetworkFeedContent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataModule {

    @Singleton
    @Provides
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            serializersModule += SerializersModule {
                polymorphicDefaultDeserializer(
                    NetworkStatusCard::class
                ) {
                    NetworkUnknownStatusCard.serializer()
                }
                polymorphicDefaultDeserializer(NetworkSubjectStatusCard::class) {
                    NetworkUnsupportedSubjectStatusCard.serializer()
                }
                polymorphicDefaultDeserializer(
                    NetworkFeedContent::class
                ) {
                    NetworkFeedContent.Unknown.serializer()
                }
                polymorphicDefaultDeserializer(
                    NetworkSubject::class
                ) {
                    NetworkUnknownSubject.serializer()
                }
                polymorphicDefaultDeserializer(
                    NetworkSubjectWithRankAndInterest::class
                ) {
                    NetworkUnknownSubjectWithRankAndInterest.serializer()
                }
                polymorphicDefaultDeserializer(NetworkSubjectModule::class) {
                    NetworkSubjectModule.SubjectOtherModule.serializer()
                }
            }
        }

    @Singleton
    @Provides
    fun providePreferenceStorage(
        @ApplicationContext context: Context,
        json: Json,
    ): PreferenceStorage =
        PreferenceStorage(context.dataStore, json)
}