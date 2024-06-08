package com.example.sehatin.feature.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sehatin.application.data.response.RecipesResponse
import com.example.sehatin.application.data.response.WeatherResponse
import com.example.sehatin.application.data.retrofit.ApiConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> get() = _weatherData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _recipesData = MutableLiveData<RecipesResponse>()
    val recipesData: LiveData<RecipesResponse> get() = _recipesData

    fun fetchWeather(lat : Double, lon : Double ,apiKey: String) {
        viewModelScope.launch {
            val client = ApiConfig.getWeatherApiService().getCurrentWeather(lat,lon,apiKey)
            client.enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                    if (response.isSuccessful) {
                        _weatherData.postValue(response.body())
                    } else {
                        _errorMessage.postValue("Failed to retrieve weather data: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    _errorMessage.postValue("Network error: ${t.message}")
                }
            })
        }
    }

    fun fetchRecipes(type: String,appId:String, appKey:String,search :String) {
        viewModelScope.launch {
            val client = ApiConfig.getRecipesApiService().getRecipes(type,appId,appKey,search)
            client.enqueue(object : Callback<RecipesResponse> {
                override fun onResponse(call: Call<RecipesResponse>, response: Response<RecipesResponse>) {
                    if (response.isSuccessful) {
                        _recipesData.postValue(response.body())
                    } else {
                        _errorMessage.postValue("Failed to retrieve recipes data: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<RecipesResponse>, t: Throwable) {
                    _errorMessage.postValue("Network error: ${t.message}")
                }
            })
        }
    }
}
