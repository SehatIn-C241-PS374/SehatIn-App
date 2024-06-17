package com.example.sehatin.application.injection

import android.health.connect.datatypes.BloodGlucoseRecord.RelationToMealType
import com.example.sehatin.BuildConfig
import com.example.sehatin.application.data.retrofit.RecipeServices
import com.example.sehatin.application.data.retrofit.ScanServices
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
import retrofit2.create
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class ScanService

@Qualifier
annotation class RecipeService

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val recipeBaseUrl = "https://api.edamam.com/api/"
    private const val foodDetailUrl = "https://sehatin-capstone-c241.et.r.appspot.com/fruitsandvegetables/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
                addInterceptor(loggingInterceptor)
            }
        }.build()
    }

    @Provides
    @ScanService
    @Singleton
    fun provideRetrofitScanServices(okHttpClient: OkHttpClient) : Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .baseUrl(foodDetailUrl)
        .build()

    @Provides
    @RecipeService
    @Singleton
    fun provideRetrofitRecipeServices(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .baseUrl(recipeBaseUrl)
        .build()
    @Provides
    @Singleton
    fun provideRecipeServices(@RecipeService retrofit: Retrofit): RecipeServices =
        retrofit.create(RecipeServices::class.java)

    @Provides
    @Singleton
    fun providesScanServices(@ScanService retrofit: Retrofit) : ScanServices =
        retrofit.create(ScanServices::class.java)

}

