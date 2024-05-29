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
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by viewModels()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, attachToParent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe the weather data

        viewModel.weatherData.observe(viewLifecycleOwner, Observer { weather ->
            Log.d("HomeFragment", "Weather data updated: ${weather.location.name}")
            binding.textCityName.text = weather.location.name
            binding.textWeatherDescription.text = weather.current.condition.text
            binding.textTemperature.text = "${weather.current.tempC}°C"

            Glide.with(requireContext())
                .load("https:${weather.current.condition.icon}")
                .into(binding.imageWeatherIcon)
        })

        // Fetch weather data
        val apiKey = getString(R.string.apikey)
        val location = getString(R.string.country)
        viewModel.fetchWeather(apiKey, location)

    }
}
