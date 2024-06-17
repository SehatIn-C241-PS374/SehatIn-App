package com.example.sehatin.application.persistance

import android.util.Log
import com.example.sehatin.application.data.base.ApiResponse
import com.example.sehatin.application.data.response.LocationResponse
import com.example.sehatin.application.data.response.ScanResponse
import com.example.sehatin.application.data.retrofit.ScanServices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ScanRepository @Inject constructor(private val apiServices: ScanServices) {

    suspend fun getFoodDetail(foodKey: String) : Flow<ApiResponse<ScanResponse>> =
    flow {
        try {
            val response = apiServices.getFoodDetail(foodKey)
            if (response.isSuccessful) {
                emit(ApiResponse.Success(response.body()!!))
            } else {
                emit(ApiResponse.Error(response.message()))
            }
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.message!!))
        }

    }

    suspend fun getAllStores(lat: Double, lon: Double) : Flow<ApiResponse<LocationResponse>> =
        flow {
            try {
                val response = apiServices.getNearestLocation(lat, lon)
                if (response.isSuccessful) {
                    emit(ApiResponse.Success(response.body()!!))
                } else {
                    emit(ApiResponse.Error(response.message()))
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.message!!))
            }

        }

}
