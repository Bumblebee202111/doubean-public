package com.github.bumblebee202111.doubean.network

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.util.Base64
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.github.bumblebee202111.doubean.BuildConfig
import com.github.bumblebee202111.doubean.coroutines.ApplicationScope
import com.github.bumblebee202111.doubean.security.DOUBAN_API_KEY
import com.github.bumblebee202111.doubean.security.DOUBAN_SECRET_KEY
import com.github.bumblebee202111.doubean.security.hmacSha1
import com.github.bumblebee202111.doubean.util.ApiResponseCallAdapterFactory
import com.github.bumblebee202111.doubean.util.AppAndDeviceInfoProvider
import com.github.bumblebee202111.doubean.util.DOUBAN_USER_AGENT_STRING
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
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.net.CookieManager
import java.net.URLEncoder
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideApirRetrofitService(json: Json): ApiRetrofitService {
        val interceptor = Interceptor { chain ->
            var request = chain.request()
            var url = request.url
            val timestamp = System.currentTimeMillis().toString()
            val valueToDigest =
                "GET&" + URLEncoder.encode(url.encodedPath, "UTF-8") + "&" + timestamp
            val signature = Base64.encodeToString(
                valueToDigest.toByteArray().hmacSha1(DOUBAN_SECRET_KEY.toByteArray()),
                Base64.NO_WRAP
            )
            url = url.newBuilder()
                .addQueryParameter("apikey", BuildConfig.DOUBAN_ACCESS_KEY)
                .addQueryParameter("channel", "Google_Market")
                .addQueryParameter("timezone", "Asia Shanghai")
                .addQueryParameter("_sig", signature)
                .addQueryParameter("_ts", timestamp)
                .build()
            request = request.newBuilder().url(url)
                .header(
                    "User-Agent",
                    DOUBAN_USER_AGENT_STRING
                )
                .header("Host", "frodo.douban.com")
                .header("Connection", "Keep-Alive")
                .build()
            chain.proceed(request)
        }

        val cookieJar = JavaNetCookieJar(CookieManager())

        val client =
            OkHttpClient().newBuilder().addInterceptor(interceptor).cookieJar(cookieJar)
                .build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addCallAdapterFactory(ApiResponseCallAdapterFactory())
            .addConverterFactory(
                json.asConverterFactory(
                    "application/json; charset=UTF8".toMediaType()
                )
            )
            .build()
            .create(ApiRetrofitService::class.java)
    }

    @Singleton
    @Provides
    fun provideCookieStorage(
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
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()

    private fun AppAndDeviceInfoProvider.getImageUA(): String {
        return "api-client/$apiClient com.douban.frodo/$doubanVersion Android/$android product/$product vendor/$vendor model/$model brand/$brand  rom/$rom  network/$network  udid/$udid  platform/$platform udid/$udid"
    }

    companion object {
        private const val BASE_URL = "https:///"
    }
}