package com.example.sehatin.application.data.retrofit

import com.example.sehatin.application.data.response.RecipesResponse
import com.example.sehatin.application.data.response.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("weather")
    fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): Call<WeatherResponse>

    @GET("recipes/v2")
    fun getRecipes(
        @Query("type")type: String,
        @Query("app_id")appId: String,
        @Query("app_key")appKey: String,
        @Query("q")search: String
    ): Call<RecipesResponse>
}