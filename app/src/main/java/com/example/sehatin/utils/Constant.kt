package com.example.sehatin.utils

import android.Manifest
import android.os.Build

const val BASE_URL = "url"

val PERMISSION_LIST_CAMERA =
    mutableListOf(
        Manifest.permission.CAMERA
    ).apply {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }.toTypedArray()

val PERMISSION_LIST_LOCATION =
    mutableListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ).toTypedArray()

