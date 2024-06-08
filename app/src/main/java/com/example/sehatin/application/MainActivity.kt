package com.example.sehatin.application

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    private lateinit var locationRequest: LocationRequest
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        checkLocationSetting()

        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.shopFragment, R.id.profileFragment)
        )



        setupBottomNavigationMenu()

//        enableEdgeToEdge()

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val hideMenus =
                destination.id == R.id.authFragment || destination.id == R.id.scanFragment || destination.id == R.id.staticImageFragment
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

    private fun checkLocationSetting() {
        locationRequest = LocationRequest.create()
        locationRequest.apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000
            fastestInterval = 2000
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result: Task<LocationSettingsResponse> =
            LocationServices.getSettingsClient(applicationContext)
                .checkLocationSettings(builder.build())

        result.addOnCompleteListener {
            try {
                val response: LocationSettingsResponse = it.getResult(ApiException::class.java)
                Toast.makeText(this@MainActivity, "GPS is On", Toast.LENGTH_SHORT).show()
            } catch (e: ApiException) {

                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        val resolvableApiException = e as ResolvableApiException
                        resolvableApiException.startResolutionForResult(
                            this@MainActivity,
                            REQUEST_CHECK_SETTING
                        )
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        // USER DEVICE DOES NOT HAVE LOCATION OPTION
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTING -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        Toast.makeText(this@MainActivity, "GPS is Turned on", Toast.LENGTH_SHORT)
                            .show()
                    }

                    Activity.RESULT_CANCELED -> {
                        Toast.makeText(
                            this@MainActivity,
                            "GPS is Required to use this app",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }

    }

    companion object {
        val REQUEST_CHECK_SETTING = 1001
    }

}