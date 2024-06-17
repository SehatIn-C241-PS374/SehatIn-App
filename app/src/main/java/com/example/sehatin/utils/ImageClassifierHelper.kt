package com.example.sehatin.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.sehatin.ml.SehatinModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.support.model.Model


class ImageClassifierHelper(private val context: Context, private val classifierListener: ClassifierListener?) {

    /**
     * Method for doing the image processing before ML inference by TFlite
     * */
    fun classifyImage(bitmap: Bitmap) {

        val model : SehatinModel by lazy {
            val options = Model.Options.Builder().setNumThreads(4).build()
            SehatinModel.newInstance(context, options)
        }

        // Creates inputs for reference.
        val image = TensorImage.fromBitmap(bitmap)
        val tensorImage = TensorImage.createFrom(image, DataType.UINT8)

        // Runs model inference and gets result.
        val outputs = model.process(tensorImage)
        Log.d("Scan", outputs.toString())
        val probability = outputs.probabilityAsCategoryList.apply {
            sortByDescending { it.score } // Sort with highest confidence first
        }.take(1)
        Log.d("Scan", probability.toString())
        classifierListener?.onResult(
            results = probability
        )

                model.close()
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResult(
            results : List<Category>?
        )
    }

}

