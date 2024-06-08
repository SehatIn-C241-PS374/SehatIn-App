package com.example.sehatin.utils

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

class ImageClassifierHelper(private val context: Context, private val classifierListener: ClassifierListener?) {

    private val threshold : Float = 0.5f
    private val modelName : String = "cancer_classification.tflite"
    private val maxResult : Int = 3
    private var imageClassifier : ImageClassifier? = null

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResult)
            .build()

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                modelName,
                optionsBuilder
            )

        } catch (e: Exception) {
            classifierListener?.onError(e.localizedMessage!!)

        }
    }

    /**
     * Method for doing the image processing before ML inference by TFlite
     * */
    fun detectObject(bitmap: Bitmap) {
        if (imageClassifier == null) {
            setupImageClassifier()
        }

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(CastOp(DataType.UINT8))
            .build()

        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))
        val result = imageClassifier?.classify(tensorImage)
        classifierListener?.onResult(
            result
        )
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResult(
            results : List<Classifications>?
        )
    }

}

