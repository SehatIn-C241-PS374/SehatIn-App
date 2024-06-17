package com.example.sehatin.feature.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sehatin.application.data.base.ApiResponse
import com.example.sehatin.application.data.response.RecipesResponse
import com.example.sehatin.application.persistance.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val recipeRepository: RecipeRepository) :
    ViewModel() {

    private val _recipesData = MutableLiveData<ApiResponse<RecipesResponse>>()
    val recipesData: LiveData<ApiResponse<RecipesResponse>> get() = _recipesData

    fun fetchRecipe() {
        viewModelScope.launch {
            recipeRepository.getHomeRecipe().collect {
                _recipesData.value = it
            }
        }
    }
}
