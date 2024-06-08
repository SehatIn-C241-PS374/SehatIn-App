package com.example.sehatin.custom

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sehatin.utils.Utils
import com.google.mlkit.vision.objects.DetectedObject


class WorkviewModel : ViewModel() {

    /**
     * This Architecture was nice, it uses 3 important component
     * the state for the UI Indicator, the object to performing the necessary API Call / Inference
     * searchedObject is the result, since I don't have the data / working with the TF lite, hence we will set with Boolean maybe
     * */
    val workflowState = MutableLiveData<WorkflowState>()
    val objectToSearch = MutableLiveData<SearchedObject>()
    val searchedObject = MutableLiveData<SearchedObject>()

    private var imageBitmap : Bitmap? = null
    private val objectThumbnailCornerRadius: Int = 12

    fun setWorkflowState(workflowState: WorkflowState) {
//        if (workflowState != WorkflowState.CONFIRMED &&
//            workflowState != WorkflowState.SEARCHING &&
//            workflowState != WorkflowState.SEARCHED)
//        {
//
//        }
        this.workflowState.value = workflowState
    }

    fun confirmingObject(searchedObject: SearchedObject, progress: Float) {
        val isConfirmed =
            progress.compareTo(1f) == 0 // Checking the value to start the progress indicator
        if (isConfirmed) {
            setWorkflowState(WorkflowState.SEARCHING)
            imageBitmap = thumbnailBitmap(searchedObject.detectedBox, searchedObject.bitmap)
            triggerSearch(SearchedObject(searchedObject.detectedBox, imageBitmap!!))

        } else {
            setWorkflowState(WorkflowState.CONFIRMING)
        }
    }

    private fun triggerSearch(confirmingObject: SearchedObject) {
        objectToSearch.value = confirmingObject
    }

    // This probably gonna call for the API Call, but since this was
    // only for classification, there's only binary result such as on the cancer
    // On the Sample Apps, it return both the detected object and the possible list, we can use that for the mapping probably
    fun onSearchCompleted(detectedObject: SearchedObject) {
        setWorkflowState(WorkflowState.SEARCHED)

        searchedObject.value = detectedObject
    }

    // Method to get the bitmap
    /**
    * This method probably will returned in error so let's see
    * */
    fun getObjectThumbnail() : Bitmap {
        return Utils.getCornerRoundedBitmap(imageBitmap!!, objectThumbnailCornerRadius)
    }

    private fun thumbnailBitmap(confirmingObject: DetectedObject, bitmap: Bitmap): Bitmap {
            val boundingBox = confirmingObject.boundingBox
            val createdBitmap = Bitmap.createBitmap(
                bitmap,
                boundingBox.left,
                boundingBox.top,
                boundingBox.width(),
                boundingBox.height()
            )
            if (createdBitmap.width > MAX_IMAGE_WIDTH) {
                val dstHeight = (MAX_IMAGE_WIDTH.toFloat() / createdBitmap.width * createdBitmap.height).toInt()
                imageBitmap = Bitmap.createScaledBitmap(createdBitmap, MAX_IMAGE_WIDTH, dstHeight, false)
            }
         return createdBitmap
    }

    enum class WorkflowState {
        NOT_STARTED,
        DETECTING,
        DETECTED,
        CONFIRMING,
        CONFIRMED,
        SEARCHING,
        SEARCHED
    }

    companion object {
        private const val MAX_IMAGE_WIDTH = 640
    }
}