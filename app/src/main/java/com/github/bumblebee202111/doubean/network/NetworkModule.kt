package com.github.bumblebee202111.doubean.network

import android.content.Context
import coil.ImageLoader
import com.github.bumblebee202111.doubean.coroutines.ApplicationScope
import com.github.bumblebee202111.doubean.security.DOUBAN_API_KEY
import com.github.bumblebee202111.doubean.util.AppAndDeviceInfoProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.addCookie
import io.ktor.http.Cookie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Singleton
    @Provides
    fun provideApiService(@ApplicationContext context: Context): ApiRetrofitService {
        return ApiRetrofitService.create()
    }

    @Singleton
    @Provides
    fun provideCookieStorage(
        @ApplicationContext context: Context,
        @ApplicationScope coroutineScope: CoroutineScope,
    ): CookiesStorage =
        AcceptAllCookiesStorage().apply {
            coroutineScope.launch {
                addCookie(
                    "https://accounts.douban.com/", Cookie(
                        name = "apiKey",
                        value = DOUBAN_API_KEY
                    )
                )
            }
        }

    @Provides
    @Singleton
    fun imageLoader(
        @ApplicationContext application: Context,
        appAndDeviceInfoProvider: AppAndDeviceInfoProvider,
    ): ImageLoader =
        ImageLoader.Builder(application)
            .okHttpClient {
                OkHttpClient.Builder().addInterceptor { chain ->
                    return@addInterceptor chain.proceed(
                        chain.request().newBuilder()
                            .addHeader("User-Agent", appAndDeviceInfoProvider.getImageUA()).build()
                    )
                }.build()
            }
            .build()

    private fun AppAndDeviceInfoProvider.getImageUA(): String {
        return "api-client/$apiClient com.douban.frodo/$doubanVersion Android/$android product/$product vendor/$vendor model/$model brand/$brand  rom/$rom  network/$network  udid/$udid  platform/$platform udid/$udid"
    }
}