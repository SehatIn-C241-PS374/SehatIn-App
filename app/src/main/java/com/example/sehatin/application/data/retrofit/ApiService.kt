package com.example.sehatin.application.data.retrofit

import com.example.sehatin.application.data.response.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("weather")
    fun getCurrentWeather(
        @Query("appid") apiKey: String,
        @Query("q") location: String
    ): Call<WeatherResponse>
}