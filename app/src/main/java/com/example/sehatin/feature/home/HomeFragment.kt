package com.example.sehatin.feature.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.sehatin.R
import com.example.sehatin.application.base.BaseFragment
import com.example.sehatin.databinding.FragmentHomeBinding
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, attachToParent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getWeatherData()
    }
    private fun getWeatherData(){
        viewModel.weatherData.observe(viewLifecycleOwner, Observer { weather ->
            binding.textCityName.text = weather.name
            binding.textWeatherDescription.text = weather.weather[0].description
            val temperatureKelvin = weather.main.temp as Double
            val temperatureCelsius = (temperatureKelvin - 273.15).toInt()
            binding.textTemperature.text = "$temperatureCelsius°C"

            Glide.with(requireContext())
                .load("https://openweathermap.org/img/w/${weather.weather[0].icon}.png")
                .into(binding.imageWeatherIcon)
        })

        // Fetch weather data
        val apiKey = getString(R.string.apikey)
        val cityName = getString(R.string.country)
        viewModel.fetchWeather(apiKey, cityName)
    }



//    override fun setupNavigation() {
//        auth = Firebase.auth
//        if (auth.currentUser == null) {
//            findNavController().navigate(R.id.auth)
//        }
//    }

    override fun setUpViews() {

        binding.fabScan.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_scanFragment)

        }

    }

    override fun setupObserver() {

    }


}

