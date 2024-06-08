package com.example.sehatin.custom

import android.graphics.Bitmap
import com.google.mlkit.vision.objects.DetectedObject

data class SearchedObject(
    val detectedBox: DetectedObject,
    val bitmap: Bitmap
)
