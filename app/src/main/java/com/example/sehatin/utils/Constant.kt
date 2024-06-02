package com.example.sehatin.utils

import android.Manifest
import android.os.Build

const val BASE_URL = "url"

val PERMISSION_LIST =
    mutableListOf(
        Manifest.permission.CAMERA
    ).apply {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }.toTypedArray()