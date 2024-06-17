package com.example.sehatin.feature.shop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sehatin.application.data.base.ApiResponse
import com.example.sehatin.application.data.response.LocationResponse
import com.example.sehatin.application.data.response.RecipesResponse
import com.example.sehatin.application.persistance.ScanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(private val repository: ScanRepository) : ViewModel() {
    private val _storesData = MutableLiveData<ApiResponse<LocationResponse>>()
    val storesData: LiveData<ApiResponse<LocationResponse>> get() = _storesData

    fun getNearestLocation(lat : Double, lon: Double) {
        viewModelScope.launch {
            repository.getAllStores(lat,lon).collect {
                _storesData.value = it
            }
        }
    }

}