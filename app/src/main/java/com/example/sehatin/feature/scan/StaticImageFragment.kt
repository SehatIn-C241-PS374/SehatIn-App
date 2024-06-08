package com.example.sehatin.feature.scan

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.example.sehatin.R
import com.example.sehatin.application.base.BaseFragment
import com.example.sehatin.custom.WorkviewModel
import com.example.sehatin.databinding.FragmentStaticImageBinding
import com.example.sehatin.utils.ImageClassifierHelper
import com.example.sehatin.utils.Toaster
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Setup the Chip Text
 * Working on the Helper and make a viewModel out of this
 * */

@AndroidEntryPoint
class StaticImageFragment : BaseFragment<FragmentStaticImageBinding>(), OnClickListener {

    private val viewModel: WorkviewModel by viewModels()
    private var currentImageUri: Uri? = null
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var currentWorkFlowState: WorkviewModel.WorkflowState? = null
    private var imageClassifierHelper : ImageClassifierHelper? = null


    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentStaticImageBinding {
        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        return FragmentStaticImageBinding.inflate(inflater, container, false)
    }

    override fun setUpViews() {
        super.setUpViews()

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.bottomNavView)
        bottomSheetBehavior?.peekHeight = 128
        bottomSheetBehavior?.isHideable = false

        binding.topActionBar.galleryButton.setOnClickListener(this)
        binding.topActionBar.closeButton.setOnClickListener(this)
        binding.bottomSheetScrimView.setOnClickListener(this)

        setupBottomNav()
        onBackHandler()

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.gallery_button -> galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            R.id.close_button -> requireActivity().onBackPressedDispatcher.onBackPressed()
            R.id.bottom_sheet_scrim_view -> bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

        }
    }

    override fun setupObserver() {
        super.setupObserver()

        viewModel.workflowState.observe(viewLifecycleOwner) { workFlowState ->
            currentWorkFlowState = workFlowState
            stateChangeMode(workFlowState)

            when (workFlowState) {
                WorkviewModel.WorkflowState.SEARCHED -> {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val imageSource = ImageDecoder.createSource(requireContext().contentResolver, currentImageUri!!)
                        ImageDecoder.decodeBitmap(imageSource)
                    } else {
                        MediaStore.Images.Media.getBitmap(requireContext().contentResolver, currentImageUri!!)
                    }.copy(Bitmap.Config.ARGB_8888, true)?.let { bitmap ->
                        analyzeImage(bitmap)
                    }


                    bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                    binding.bottomSheetScrimView.invalidate()
                }

                else -> {
                }

            }

        }

    }

    private fun analyzeImage(bitmap: Bitmap) {
        imageClassifierHelper = ImageClassifierHelper(requireContext(), object: ImageClassifierHelper.ClassifierListener {
            override fun onError(error: String) {
                Toaster.show(requireContext(), error)
            }

            override fun onResult(results: List<Classifications>?) {
                results?.let {classifications ->
                    if (classifications.isNotEmpty() and classifications[0].categories.isNotEmpty()) {
                        val sortedCategories = classifications[0].categories

                        Log.d("Scan", sortedCategories.toString())
                        if (sortedCategories[0].label.isNotEmpty()) {
                            Log.d("Scan", sortedCategories[0].toString())
//                            stuff(sortedCategories)
                            binding.bottomSheet.bottomSheetTitle1.text = sortedCategories[0].toString()
                        } else {
                            Toaster.show(
                                requireContext(),
                                "Something was off, try it again"
                            )
                        }

                    }
                }
            }

        })

        imageClassifierHelper?.detectObject(bitmap)

    }

    private fun setupBottomNav() {
        bottomSheetBehavior?.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomView: View, newState: Int) {
                    binding.bottomSheetScrimView.visibility =
                        if (newState == BottomSheetBehavior.STATE_COLLAPSED) View.GONE
                        else View.VISIBLE
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            }
        )
    }

    private fun stateChangeMode(workFlowState: WorkviewModel.WorkflowState?) {
        val promptChip = binding.bottomPromptChip

        when (workFlowState) {
            WorkviewModel.WorkflowState.DETECTED, WorkviewModel.WorkflowState.SEARCHED -> {
                promptChip.visibility = View.VISIBLE
                promptChip.setText(
                    // Should I set the detecting or what?
                    if (workFlowState == WorkviewModel.WorkflowState.DETECTED) {
                        "Please wait a for a moment"
                    } else {
                        "Pick the image to classify"
                    }
                )
            }

            else -> {}
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.setWorkflowState(WorkviewModel.WorkflowState.DETECTING)
            Toaster.show(requireContext(), "Success")
            cropImage(uri)

        } else {
            Toaster.show(requireContext(), "Failed")
        }

    }

    private fun cropImage(uri: Uri) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFile = File(requireContext().filesDir, "Photos/$timestamp.jpg")
        if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()

        val imageUri = Uri.fromFile(imageFile)

        val uCrop = UCrop.of(uri, imageUri)
            .withAspectRatio(2f, 3f)

        uCrop.getIntent(requireContext()).apply {
            launcherUCrop.launch(this)
        }
    }

    private val launcherUCrop = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            currentImageUri = UCrop.getOutput(result.data!!)

            binding.ivPreview.setImageURI(currentImageUri)
            viewModel.setWorkflowState(WorkviewModel.WorkflowState.SEARCHED)
        }

        if (result.resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(result.data!!)
            Toaster.show(requireContext(), cropError?.localizedMessage!!)
        }
    }

    private fun onBackHandler() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (bottomSheetBehavior?.state != BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                } else {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }

}