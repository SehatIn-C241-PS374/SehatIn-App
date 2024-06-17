package com.example.sehatin.feature.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sehatin.application.data.base.ApiResponse
import com.example.sehatin.application.data.response.RecipeItem
import com.example.sehatin.application.persistance.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentDetailViewModel @Inject constructor(private val recipeRepository: RecipeRepository) : ViewModel() {

    private val _recipesData = MutableLiveData<ApiResponse<RecipeItem>>()
    val recipesData: LiveData<ApiResponse<RecipeItem>> get() = _recipesData

    fun getDetailRecipe(recipeId: String) {
        viewModelScope.launch {
            recipeRepository.getDetailRecipe(recipeId).collect {
                _recipesData.value = it
            }
        }
    }
}