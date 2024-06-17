package com.example.sehatin.application.data.retrofit

import com.example.sehatin.BuildConfig
import com.example.sehatin.application.data.response.RecipeItem
import com.example.sehatin.application.data.response.RecipesResponse
import com.example.sehatin.utils.randomSearch
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeServices {
    @GET("recipes/v2")
    suspend fun getRecipes(
        @Query("type") type: String = "public",
        @Query("app_id") appId: String = BuildConfig.APP_ID_RECIPE,
        @Query("app_key") appKey: String = BuildConfig.RECIPE_KEY_API,
        @Query("q") search: String
    ): Response<RecipesResponse>

    @GET("recipes/v2/{recipeId}")
    suspend fun getDetailRecipe(
        @Path("recipeId") recipeId: String,
        @Query("type") type: String = "public",
        @Query("app_id") appId: String = BuildConfig.APP_ID_RECIPE,
        @Query("app_key") appKey: String = BuildConfig.RECIPE_KEY_API,
    ) : Response<RecipeItem>
}