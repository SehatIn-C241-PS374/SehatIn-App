package com.example.sehatin.custom

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import java.io.ByteArrayOutputStream

abstract class BaseImageAnalyzer<T> : ImageAnalysis.Analyzer {

    abstract val graphicOverlay: GraphicOverlay

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        mediaImage?.let {
            val inputImage = InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees)
            detectInImage(inputImage)
                .addOnSuccessListener { results ->
                    graphicOverlay.setPreviewDimension(imageProxy.width, imageProxy.height)
                    onSuccess(
                        results,
                        graphicOverlay,
                        imageProxy.toBitmap()
                    )
                    imageProxy.close()
                }
                .addOnFailureListener {error ->
                    onFailure(error)
                    imageProxy.close()
                }
        }
    }

    protected abstract fun detectInImage(image: InputImage): Task<T>

    abstract fun stop()

    abstract fun onSuccess(
        results: T,
        graphicsOverlay: GraphicOverlay,
        bitmap: Bitmap
    )

    abstract fun onFailure(e: Exception)
}