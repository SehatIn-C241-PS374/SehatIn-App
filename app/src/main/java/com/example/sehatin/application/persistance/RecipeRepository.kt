package com.example.sehatin.application.persistance

import android.util.Log
import com.example.sehatin.application.data.base.ApiResponse
import com.example.sehatin.application.data.response.RecipeItem
import com.example.sehatin.application.data.response.RecipesResponse
import com.example.sehatin.application.data.retrofit.RecipeServices
import com.example.sehatin.utils.randomSearch
import com.google.android.play.integrity.internal.q
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RecipeRepository @Inject constructor(private val apiService: RecipeServices) {

    suspend fun getHomeRecipe(foodKey: String = randomSearch()): Flow<ApiResponse<RecipesResponse>> =
    flow {
        try {
            emit(ApiResponse.Loading)
            val response = apiService.getRecipes(search = foodKey)
            if (response.isSuccessful) {
                emit(ApiResponse.Success(response.body()!!))
            } else {
                emit(ApiResponse.Error(response.message()))
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.toString())
        }
    }

    suspend fun getDetailRecipe(recipeId: String) : Flow<ApiResponse<RecipeItem>> =
        flow {
            try {
                val response = apiService.getDetailRecipe(recipeId)
                if (response.isSuccessful) {
                    emit(ApiResponse.Success(response.body()!!))
                } else {
                    emit(ApiResponse.Error(response.message()))
                }
            } catch (e: Exception) {
                ApiResponse.Error(e.toString())
            }

        }
}