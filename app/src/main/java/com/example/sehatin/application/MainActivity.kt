package com.example.sehatin.application

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.example.sehatin.R
import com.example.sehatin.databinding.ActivityMainBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var keepOnSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
            .setKeepOnScreenCondition { keepOnSplashScreen }
        Handler(Looper.getMainLooper()).postDelayed(({ keepOnSplashScreen = false }), 1500)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.shopFragment, R.id.profileFragment)
        )

        setupBottomNavigationMenu()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val hideMenus =
                destination.id == R.id.authFragment || destination.id == R.id.detailFragment || destination.id == R.id.scanFragment || destination.id == R.id.staticImageFragment
            hideNavigation(hideMenus)
        }

    }

    private fun hideNavigation(hideMenus: Boolean) {
        if (hideMenus) binding.bottomNavView.visibility = View.GONE
        else binding.bottomNavView.visibility = View.VISIBLE
    }

    private fun setupBottomNavigationMenu() {
        val bottomNavView = binding.bottomNavView
        bottomNavView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
    }

}