package com.example.sehatin.feature.scan

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sehatin.application.data.base.ApiResponse
import com.example.sehatin.application.data.response.RecipesResponse
import com.example.sehatin.application.data.response.ScanResponse
import com.example.sehatin.application.persistance.RecipeRepository
import com.example.sehatin.application.persistance.ScanRepository
import com.example.sehatin.custom.SearchedObject
import com.example.sehatin.utils.Utils
import com.google.mlkit.vision.objects.DetectedObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val scanRepository: ScanRepository,
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    /**
     * This Architecture was nice, it uses 3 important component
     * the state for the UI Indicator, the object to performing the necessary API Call / Inference
     * searchedObject is the result, since I don't have the data / working with the TF lite, hence we will set with Boolean maybe
     * */
    val workflowState = MutableLiveData<WorkflowState>()
    val objectToSearch = MutableLiveData<SearchedObject>()

    private val _detailFood = MutableLiveData<ApiResponse<ScanResponse>>()
    val detailFood: LiveData<ApiResponse<ScanResponse>> get() = _detailFood

    private val _recipesData = MutableLiveData<ApiResponse<RecipesResponse>>()
    val recipesData: LiveData<ApiResponse<RecipesResponse>> get() = _recipesData

    enum class WorkflowState {
        DETECTING,
        DETECTED,
        CONFIRMING,
        CONFIRMED,
        SEARCHING,
        SEARCHED
    }

    private var imageBitmap: Bitmap? = null
    private val objectThumbnailCornerRadius: Int = 12

    fun setWorkflowState(workflowState: WorkflowState) {
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

    fun getObjectThumbnail(): Bitmap {
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
            val dstHeight =
                (MAX_IMAGE_WIDTH.toFloat() / createdBitmap.width * createdBitmap.height).toInt()
            imageBitmap =
                Bitmap.createScaledBitmap(createdBitmap, MAX_IMAGE_WIDTH, dstHeight, false)
        }
        return createdBitmap
    }


    fun getDetailFood(foodKey: String) {
        viewModelScope.launch {
            scanRepository.getFoodDetail(foodKey).collect {
                _detailFood.value = it
            }
        }
    }

    fun fetchRecipe(foodKey: String) {
        viewModelScope.launch {
            recipeRepository.getHomeRecipe(foodKey).collect {
                _recipesData.value = it
            }
        }
    }

    companion object {
        private const val MAX_IMAGE_WIDTH = 640
    }


}