package com.example.sehatin.application.injection

import com.example.sehatin.BuildConfig
import com.example.sehatin.application.persistance.DataStoreManager
import com.example.sehatin.utils.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    const val baseUrl = BASE_URL

    @Provides
    @Singleton
    fun provideOkHttpClient(dataStoreManager: DataStoreManager): OkHttpClient {
        val headerInjector = Interceptor { chain ->
            return@Interceptor chain.proceed(
                chain.request()
                    .newBuilder()
                    .header(
                        "Authorization",
                        runBlocking { dataStoreManager.getUserToken() ?: "" }
                    )
                    .build()
            )
        }
        return OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
                addInterceptor(loggingInterceptor)
            }
            addInterceptor(headerInjector)
        }.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient) : Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .baseUrl(baseUrl)
        .build()

    /**
     * Still waiting for the API....
     * */
//    @Provides
    fun provideApiService() {}
}