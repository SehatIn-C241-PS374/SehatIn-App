package com.example.sehatin.feature.scan

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import androidx.fragment.app.viewModels
import android.provider.Settings
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.sehatin.R
import com.example.sehatin.application.base.BaseFragment
import com.example.sehatin.custom.ObjectDetectorGraphic
import com.example.sehatin.custom.ObjectDetectorProcessor
import com.example.sehatin.custom.WorkviewModel
import com.example.sehatin.databinding.FragmentScanBinding
import com.example.sehatin.utils.PERMISSION_LIST
import com.example.sehatin.utils.Toaster
import com.example.sehatin.utils.showsPermission
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import dagger.hilt.android.AndroidEntryPoint
import java.util.Objects
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class ScanFragment : BaseFragment<FragmentScanBinding>() {

    private val viewModel: WorkviewModel by viewModels()
    private var currentWorkflowState: WorkviewModel.WorkflowState? = null

    private var preview: Preview? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageAnalyzer: ImageAnalysis? = null

    /**
     * Takes the arguments list manifest permission
     *
     * return the granted method if success, and dialog message if the user decline
     * */
    private val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permission ->
            val allPermissionGranted = permission.all { it.value }

            if (allPermissionGranted) {
                showsUi(true)
                startCamera()
            } else {
                val permissionList = permission.filter { !it.value }.keys
                handlePermissionResult(permissionList)
            }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri : Uri? ->
        if (uri != null) {
            Toaster.show(requireContext(), "Yey")
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentScanBinding {
        permissionLauncher.launch(PERMISSION_LIST)
        return FragmentScanBinding.inflate(inflater, container, false)
    }

    override fun setUpViews() {

        if (checkSelfPermission(PERMISSION_LIST)) {
            startCamera()
            showsUi(true)
            binding.actionBar.galleryButton.setOnClickListener {
                galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            setupWorkflowModel()

        } else {
            showsUi(false)
            binding.btnRequest.setOnClickListener {
                permissionLauncher.launch(PERMISSION_LIST)
            }
        }
    }

    private fun startCamera() {
        /**
         * Setup with CameraController
         * */
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            preview = Preview.Builder()
                .build()

            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .apply {
                    setAnalyzer(
                        Executors.newSingleThreadExecutor(),
                        selectAnalyzer()
                    )
                }

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            setCameraConfig(cameraProvider, cameraSelector)

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun selectAnalyzer() : ImageAnalysis.Analyzer {
        return ObjectDetectorProcessor(binding.cameraOverlay.cameraPreviewGraphicOverlay, viewModel)
    }

    private fun setCameraConfig(cameraProvider: ProcessCameraProvider?, cameraSelector: CameraSelector) {
        try {
            cameraProvider?.unbindAll()
            camera = cameraProvider?.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageAnalyzer
            )
            preview?.setSurfaceProvider(binding.previewView.surfaceProvider)
        } catch (e: Exception) {
            Log.e(TAG, "Use case binding failed", e)
        }
    }

    private fun setupWorkflowModel() {
        viewModel.workflowState.observe(viewLifecycleOwner) {workflowState ->
            if (workflowState == null || Objects.equals(currentWorkflowState, workflowState))
            {
                return@observe
            }
            currentWorkflowState = workflowState

            stateChangeInAutoMode(workflowState)
        }
    }

    // Do u also need to start the camera here? I guess not with CameraX
    private fun stateChangeInAutoMode(workflowState: WorkviewModel.WorkflowState) {
        val promptChip = binding.cameraOverlay.bottomPromptChip
        val wasPromptChipGone = promptChip.visibility == View.GONE

        binding.cameraOverlay.searchProgressBar.visibility = View.GONE

        when (workflowState) {
            WorkviewModel.WorkflowState.DETECTING, WorkviewModel.WorkflowState.DETECTED, WorkviewModel.WorkflowState.CONFIRMING -> {
                promptChip.visibility = View.VISIBLE
                promptChip.setText(
                    if (workflowState == WorkviewModel.WorkflowState.CONFIRMING)
                        R.string.prompt_hold_camera_steady
                    else
                        R.string.prompt_point_at_an_object
                )
            }
            WorkviewModel.WorkflowState.CONFIRMED -> {
                promptChip.visibility = View.VISIBLE
                promptChip.setText(R.string.prompt_searching)
            }
            WorkviewModel.WorkflowState.SEARCHING -> {
                binding.cameraOverlay.searchProgressBar.visibility = View.VISIBLE
                promptChip.visibility = View.VISIBLE
                promptChip.setText(R.string.prompt_searching)
            }
            WorkviewModel.WorkflowState.SEARCHED -> {
                promptChip.visibility = View.GONE
            }
            else -> promptChip.visibility = View.GONE


        }
    }


    private fun handlePermissionResult(permissionList: Set<String>) {
        if (permissionList.any { shouldShowRequestPermissionRationale(it) }) {
            val requirement = permissionList.joinToString(", ") { it.substringAfter(".") }
            showsPermission(
                title = "Permission Required",
                description = "This app require the $requirement in order to work",
                positiveButtontitle = "Grant Permission",
                positiveButton = { permissionLauncher.launch(PERMISSION_LIST) }
            )
        } else {
            showsPermission(
                title = "Permission Denied",
                description = "Permissions were not granted. You can enable them in app settings.",
                positiveButtontitle = "Grant Permission",
                positiveButton = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.fromParts("package", activity?.packageName, null)
                    startActivity(intent)
                }
            )

        }

    }

    private fun checkSelfPermission(permission: Array<String>): Boolean {
        permission.forEach {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    it
                ) != PackageManager.PERMISSION_GRANTED
            ) return false
        }
        return true
    }

    private fun showsUi(permissionGranted: Boolean) {
        if (permissionGranted) {
            binding.actionBar.topActionBar.visibility = View.VISIBLE
            binding.tvDeclinedPermission.visibility = View.GONE
            binding.btnRequest.visibility = View.GONE
        } else {
            binding.actionBar.topActionBar.visibility = View.GONE
            binding.tvDeclinedPermission.visibility = View.VISIBLE
            binding.btnRequest.visibility = View.VISIBLE
        }
    }

    companion object {
        const val TAG = "CameraXDemo"
    }

}
