package com.example.sehatin.application.data.retrofit

import com.example.sehatin.application.data.response.LocationResponse
import com.example.sehatin.application.data.response.ScanResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ScanServices {

    @GET("/fruitsandvegetables/{foodScan}")
    suspend fun getFoodDetail(
        @Path("foodScan") foodScan : String
    ) : Response<ScanResponse>

    @GET("/stores/location")
    suspend fun getNearestLocation(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double
    ) : Response<LocationResponse>
}