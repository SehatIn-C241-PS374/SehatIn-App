package com.example.sehatin.custom

import android.annotation.SuppressLint
import android.graphics.Rect
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions

class ObjectDetectorProcessor(
    private val isMutlitpleBoolean: Boolean,
    private val listener: (MutableList<DetectedObject>, image: Rect) -> Unit
) : ImageAnalysis.Analyzer {

    private val options = ObjectDetectorOptions.Builder()
        .apply {
            setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
            if (isMutlitpleBoolean) enableMultipleObjects()
        }.build()

    private val detector = ObjectDetection.getClient(options)
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        detector.process(image)
            .addOnSuccessListener {objectList ->
                listener(objectList, image.mediaImage?.cropRect!!)
            }
            .addOnFailureListener {
                Log.e(TAG, "Error: ${it.message}", it)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
    companion object {
        private const val TAG = "ObjectAnalyser"
    }


    //    private val options = ObjectDetectorOptions.Builder()
//        .apply {
//            setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
//            enableClassification()
//        }.build()
//
//    private val detector = ObjectDetection.getClient(options)
//    override val graphicOverlay: GraphicOverlay
//        get() = view
//
//    override fun detectInImage(image: InputImage): Task<List<DetectedObject>> {
//        return detector.process(image)
//    }
//
//    override fun stop() {
//        detector.close()
//    }
//
//    override fun onSuccess(
//        result: List<DetectedObject>,
//        graphicsOverlay: GraphicOverlay,
//        rect: Rect
//    ) {
//        Log.d("ObjectDetectorProcessor", "On Success")
//        graphicOverlay.clear()
//        result.forEach {
//            val objectDetection = ObjectDetectorGraphic(graphicsOverlay, it)
//        graphicsOverlay.add(objectDetection)
//        }
//        graphicOverlay.postInvalidate()
//    }
//
//    override fun onFailure(e: Exception) {
//     Log.d("Object Detector Processor", e.toString())
//    }
}