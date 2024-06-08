package com.example.sehatin.feature.shop

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import android.app.Activity
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.sehatin.R
import com.example.sehatin.application.base.BaseFragment
import com.example.sehatin.databinding.FragmentShopBinding
import com.example.sehatin.utils.Maps
import com.example.sehatin.utils.PERMISSION_LIST_LOCATION
import com.example.sehatin.utils.Utils
import com.example.sehatin.utils.checkSelfPermission
import com.example.sehatin.utils.showsPermission
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

/*
* Notes:
* - 1 thing to notice is how do we can pass the data as the dialog (do we can use the dialog here or separate dialog) ?
* - how to obtain the data from the marker and used the data for the dialog and location for maps
* - How do I can use the check in for the maps functionality (fused, isMyLoocation and etc)
* */

@AndroidEntryPoint
class ShopFragment : BaseFragment<FragmentShopBinding>(), OnMapReadyCallback, OnClickListener,
    OnMarkerClickListener {

    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var boundsBuilder = LatLngBounds.Builder()
    private var gMap : GoogleMap? = null
    val markers = Utils.getShopData()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentShopBinding {
        permissionLauncher.launch(PERMISSION_LIST_LOCATION)
        return FragmentShopBinding.inflate(inflater, container, false)
    }

    override fun setUpViews() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (checkSelfPermission(PERMISSION_LIST_LOCATION)) {
            showsUi(true)
            checkLocationSettings()
            val mapFragment = childFragmentManager.findFragmentById(R.id.gMaps) as SupportMapFragment?
            mapFragment?.getMapAsync(this)
            binding.fabNavigate.setOnClickListener(this)
            binding.btnNavigate.setOnClickListener(this)
        } else {
            showsUi(false)
            binding.btnRequest.setOnClickListener(this)
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap

        markers.forEach {
            val latLng = LatLng(it.lat, it.lon)
             googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(it.name)
                    .snippet(it.description)

            )?.tag = it

            boundsBuilder.include(latLng)
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )

        googleMap.setOnMarkerClickListener(this)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        bottomSheetBehavior?.peekHeight = binding.gMaps.height.div(3)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        val index = marker.tag as Maps
        binding.tvTitle.text = index.name
            binding.tvDescription.text = index.description
            binding.btnNavigate.setOnClickListener {
                val gmmIntentUri = Uri.parse("geo:${index.lat},${index.lon}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                mapIntent.resolveActivity(requireActivity().packageManager)?.let {
                    startActivity(mapIntent)
                }
            }
        return true
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnRequest -> permissionLauncher.launch(PERMISSION_LIST_LOCATION)
            R.id.fabNavigate -> {
                if (checkSelfPermission(PERMISSION_LIST_LOCATION)) {
                    fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener {location ->
                            val latLng = LatLng(location.latitude, location.longitude)
                            val cameraUpdate = CameraUpdateFactory.newLatLng(latLng)
                            gMap?.animateCamera(cameraUpdate)
                        }
                }
            }

        }
    }



    private fun checkLocationSettings() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // Let's see if this working
            interval = 5000
            fastestInterval = 2000
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result: Task<LocationSettingsResponse> =
            LocationServices.getSettingsClient(requireContext())
                .checkLocationSettings(builder.build())

        result.addOnSuccessListener {
            if (checkSelfPermission(PERMISSION_LIST_LOCATION)) {
                fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { location ->
                        location?.let {
                            gMap?.uiSettings?.isMyLocationButtonEnabled = false
                            gMap?.isMyLocationEnabled = true
                            val userLocation = LatLng(location.latitude, location.longitude)
                            val cameraUpdate = CameraUpdateFactory.newLatLng(userLocation)
                            gMap?.animateCamera(cameraUpdate)
                        }
                    }
            }

        }

        result.addOnFailureListener { e ->
            val exception = e as ApiException
            when (exception.statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    val resolvableApiExecution = e as ResolvableApiException
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(resolvableApiExecution.resolution).build()
                    locationSettingsLauncher.launch(intentSenderRequest)
                }
            }

        }

    }

    private fun showsUi(permission: Boolean) {
        if (permission) {
            binding.gMaps.visibility = View.VISIBLE
            binding.bottomNavView.visibility = View.VISIBLE
            binding.tvDeclinedPermission.visibility = View.GONE
            binding.btnRequest.visibility = View.GONE
            bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomNavView)
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            binding.gMaps.visibility = View.GONE
            binding.bottomNavView.visibility = View.GONE
            binding.tvDeclinedPermission.visibility = View.VISIBLE
            binding.btnRequest.visibility = View.VISIBLE
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN

        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionGranted ->
        when {
            permissionGranted[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                setUpViews()
            }

            permissionGranted[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                setUpViews()
            }

            else -> {
                handlePermissionResult()
            }

        }
    }

    private val locationSettingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(requireContext(), "GPS is Turned on", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "GPS is Required to use this app", Toast.LENGTH_SHORT)
                .show()

        }

    }

    private fun handlePermissionResult() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            showsPermission(
                title = "Permission Required",
                description = "This app require the Location access in order to work",
                positiveButtontitle = "Grant Permission",
                positiveButton = { permissionLauncher.launch(PERMISSION_LIST_LOCATION) }
            )
        } else {
            showsPermission(
                title = "Permission Denied",
                description = "Permissions were not granted. You can enable them in app settings.",
                positiveButtontitle = "Grant Permission",
                positiveButton = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.fromParts("package", activity?.packageName, null)
                    startActivity(intent)
                }
            )

        }
    }



}