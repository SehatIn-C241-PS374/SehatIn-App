package com.example.sehatin.feature.scan

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.fragment.app.viewModels
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sehatin.R
import com.example.sehatin.application.base.BaseFragment
import com.example.sehatin.application.data.base.ApiResponse
import com.example.sehatin.application.data.response.HistoryData
import com.example.sehatin.custom.GraphicOverlay
import com.example.sehatin.custom.ObjectDetectorProcessor
import com.example.sehatin.databinding.FragmentScanBinding
import com.example.sehatin.feature.adapter.RecipesAdapter
import com.example.sehatin.utils.ImageClassifierHelper
import com.example.sehatin.utils.PERMISSION_LIST_CAMERA
import com.example.sehatin.utils.Toaster
import com.example.sehatin.utils.checkSelfPermission
import com.example.sehatin.utils.formatCurrentDate
import com.example.sehatin.utils.formatEnglishLabel
import com.example.sehatin.utils.showsPermission
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import org.tensorflow.lite.support.label.Category
import java.util.Objects
import java.util.concurrent.Executors

/**
 * Setup the Custom View Scrim?
 * Refactoring the code and uses the width?
 * Setup the Adapter, do I need to make a custom Dialog for this? Mager bjir
 * Mana Firestore belum lagi
 * */
@AndroidEntryPoint
class ScanFragment : BaseFragment<FragmentScanBinding>(), View.OnClickListener,
    RecipesAdapter.OnItemClickListener {

    private val viewModel: ScanViewModel by viewModels()
    private var currentWorkflowState: ScanViewModel.WorkflowState? = null
    private var previewView: PreviewView? = null
    private var preview: Preview? = null
    private var camera: Camera? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var isEnableTorch = false
    private var imageAnalyzer: ImageAnalysis? = null
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var objectThumbnailForBottomSheet: Bitmap? = null
    private var slidingSheetUpFromHiddenState = false
    private var imageClassifierHelper: ImageClassifierHelper? = null
    private var user: FirebaseUser? = Firebase.auth.currentUser
    private var db: FirebaseFirestore = Firebase.firestore
    private var userRef: DocumentReference = db.collection("users").document(user?.uid!!)
    private var isDetected: Boolean = true
    private lateinit var historyData: HistoryData

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentScanBinding {
        permissionLauncher.launch(PERMISSION_LIST_CAMERA)
        return FragmentScanBinding.inflate(inflater, container, false)
    }

    override fun setUpViews() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomNav.bottomNavView)

        if (checkSelfPermission(PERMISSION_LIST_CAMERA)) {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            startCamera()
            showsUi(true)
            binding.actionBar.galleryButton.setOnClickListener {
                findNavController().navigate(R.id.staticImageFragment)
            }
            setupBottomSheet()
            onBackHandler()

            binding.actionBar.flashButton.setOnClickListener(this)
            binding.actionBar.closeButton.setOnClickListener(this)

        } else {
            showsUi(false)
            binding.btnRequest.setOnClickListener {
                permissionLauncher.launch(PERMISSION_LIST_CAMERA)
            }
        }
    }

    override fun setupObserver() {
        super.setupObserver()

        viewModel.workflowState.observe(viewLifecycleOwner) { workflowState ->
            if (workflowState == null || Objects.equals(currentWorkflowState, workflowState)) {
                return@observe
            }
            currentWorkflowState = workflowState

            listenForStateChange(workflowState)
        }

        /**
         * This was performing necessary call for the API / ML Inference, but for this case just pass them
         * */
        viewModel.objectToSearch.observe(viewLifecycleOwner) { detectObject ->
            analyzeImage(bitmap = detectObject.bitmap)
        }

        viewModel.detailFood.observe(viewLifecycleOwner) { status ->
            when (status) {
                is ApiResponse.Success -> {
                    val data = status.data.data?.item
                    with(binding.bottomNav) {

                        tvBotttomSheetTitle.text = data?.name
                        tvBotttomSheetDesc.text = data?.description
                        tvCaloriesValue.text = getString(R.string.detail_calori_value, data?.nutrition?.calories)
                        tvCarbs.text = getString(R.string.detail_protein_value, data?.nutrition?.carbohydrates)
                        tvFiber.text = getString(R.string.detail_cholesterol_value, data?.nutrition?.fiber)

                        if (isDetected) {
                            historyData = HistoryData(
                                formatCurrentDate(),
                                data?.name!!,
                                data.nutrition?.calories!!,
                                data.nutrition.carbohydrates!!,
                                data.nutrition.fiber!!
                            )

                            userRef.collection("history").add(historyData).addOnSuccessListener {}

                            binding.loadingIndicator.hide()
                            bottomNavView.visibility = View.VISIBLE

                        }


                    }


                }

                is ApiResponse.Error -> {
                    Toaster.show(requireContext(), status.error)
                }

                ApiResponse.Loading -> {}
            }
        }

        viewModel.recipesData.observe(viewLifecycleOwner) { recipesResponse ->
            when (recipesResponse) {
                is ApiResponse.Success -> {
                    val recipeAdapter = RecipesAdapter(
                        recipesResponse.data.item,
                        this
                    )
                    binding.bottomNav.rvRecomendationDish.layoutManager =
                        LinearLayoutManager(requireContext())
                    binding.bottomNav.rvRecomendationDish.adapter = recipeAdapter

                    binding.loadingIndicator.hide()
                    previewView = binding.previewView
                    isDetected = false
                    slidingSheetUpFromHiddenState = true
                    objectThumbnailForBottomSheet = viewModel.getObjectThumbnail()
                    bottomSheetBehavior?.peekHeight =
                        previewView?.height?.div(2) ?: BottomSheetBehavior.PEEK_HEIGHT_AUTO
                    bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

                }

                is ApiResponse.Error -> {
                    binding.loadingIndicator.hide()
                    Toaster.show(requireContext(), "Something was off")
                }

                ApiResponse.Loading -> {}
            }
        }


    }

    private fun analyzeImage(bitmap: Bitmap) {
        imageClassifierHelper = ImageClassifierHelper(
            requireContext(),
            object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    Toaster.show(requireContext(), error)
                }

                override fun onResult(results: List<Category>?) {
                    results?.let { classification ->
                        if (classification.isNotEmpty()) {
                            binding.loadingIndicator.show()
                            val label = classification.first().label.trim()
                            viewModel.getDetailFood(label)
                            viewModel.fetchRecipe(formatEnglishLabel(label))
                        }
                    }

                }

            })

        imageClassifierHelper?.classifyImage(bitmap)
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior?.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    binding.bottomSheetScrimView.visibility =
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) View.GONE else View.VISIBLE
                    binding.cameraOverlay.cameraPreviewGraphicOverlay.clear()

                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            viewModel.setWorkflowState(ScanViewModel.WorkflowState.DETECTING)
                            startCamera()
                        }

                        BottomSheetBehavior.STATE_COLLAPSED,
                        BottomSheetBehavior.STATE_EXPANDED,
                        BottomSheetBehavior.STATE_HALF_EXPANDED -> slidingSheetUpFromHiddenState =
                            false

                        else -> {}
                    }
                }

                override fun onSlide(bottomView: View, slideOffset: Float) {
                    val searchedObject = viewModel.objectToSearch.value
                    if (searchedObject == null || java.lang.Float.isNaN(slideOffset)) {
                        return
                    }

                    val bottomSheetView = bottomSheetBehavior ?: return
                    val collapsedStateHeight =
                        bottomSheetView.peekHeight.coerceAtMost(bottomView.height)
                    val bottomBitmap = objectThumbnailForBottomSheet ?: return
                    val graphicOverlay = binding.cameraOverlay.cameraPreviewGraphicOverlay

                    if (slidingSheetUpFromHiddenState) {
                        val thumbnailRect = graphicOverlay.calculateRect(
                            graphicOverlay,
                            searchedObject.detectedBox.boundingBox
                        )
                        binding.bottomSheetScrimView.updateWithThumbnailTranslateAndScale(
                            bottomBitmap,
                            collapsedStateHeight,
                            slideOffset,
                            thumbnailRect
                        )
                    } else {
                        binding.bottomSheetScrimView.updateWithThumbnailTranslate(
                            bottomBitmap, collapsedStateHeight, slideOffset, bottomView
                        )
                    }
                }
            }
        )

        binding.bottomSheetScrimView.setOnClickListener(this)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        graphicOverlay = binding.cameraOverlay.cameraPreviewGraphicOverlay

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            preview = Preview.Builder()
                .build()

            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageRotationEnabled(true)
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

    private fun setCameraConfig(
        cameraProvider: ProcessCameraProvider?,
        cameraSelector: CameraSelector
    ) {
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
            Toaster.show(requireContext(), e.toString())
        }
    }

    private fun selectAnalyzer(): ImageAnalysis.Analyzer {
        return ObjectDetectorProcessor(binding.cameraOverlay.cameraPreviewGraphicOverlay, viewModel)
    }

    private fun listenForStateChange(workflowState: ScanViewModel.WorkflowState) {
        val promptChip = binding.cameraOverlay.bottomPromptChip

        binding.cameraOverlay.searchProgressBar.visibility = View.GONE

        when (workflowState) {
            ScanViewModel.WorkflowState.DETECTING, ScanViewModel.WorkflowState.DETECTED, ScanViewModel.WorkflowState.CONFIRMING -> {
                promptChip.visibility = View.VISIBLE
                promptChip.setText(
                    if (workflowState == ScanViewModel.WorkflowState.CONFIRMING)
                        R.string.prompt_hold_camera_steady
                    else
                        R.string.prompt_point_at_an_object
                )
            }

            ScanViewModel.WorkflowState.CONFIRMED -> {
                promptChip.visibility = View.VISIBLE
                promptChip.setText(R.string.prompt_searching)
            }

            ScanViewModel.WorkflowState.SEARCHING -> {
                binding.cameraOverlay.searchProgressBar.visibility = View.VISIBLE
                promptChip.visibility = View.VISIBLE
                promptChip.setText(R.string.prompt_searching)
                cameraProvider?.unbindAll()
            }

            ScanViewModel.WorkflowState.SEARCHED -> {
                promptChip.visibility = View.GONE
                cameraProvider?.unbindAll()
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
                positiveButton = { permissionLauncher.launch(PERMISSION_LIST_CAMERA) }
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

    private fun showsUi(permissionGranted: Boolean) {
        if (permissionGranted) {
            binding.actionBar.topActionBar.visibility = View.VISIBLE
            binding.tvDeclinedPermission.visibility = View.GONE
            binding.btnRequest.visibility = View.GONE
            binding.bottomNav.bottomNavView.visibility = View.VISIBLE
        } else {
            binding.actionBar.topActionBar.visibility = View.GONE
            binding.tvDeclinedPermission.visibility = View.VISIBLE
            binding.btnRequest.visibility = View.VISIBLE
            binding.bottomNav.bottomNavView.visibility = View.GONE
        }
    }


    private fun onBackHandler() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (bottomSheetBehavior?.state != BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
                    isDetected = true
                    startCamera()
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


    override fun onClick(view: View) {
        when (view.id) {
            R.id.bottom_sheet_scrim_view -> {
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
                isDetected = true
                startCamera()
            }

            R.id.flash_button -> {
                if (camera?.cameraInfo?.hasFlashUnit()!!) {
                    if (isEnableTorch) {
                        isEnableTorch = !isEnableTorch
                        camera?.cameraControl?.enableTorch(isEnableTorch)
                        binding.actionBar.flashButton.setImageResource(R.drawable.ic_flash_off)
                    } else {
                        isEnableTorch = !isEnableTorch
                        camera?.cameraControl?.enableTorch(isEnableTorch)
                        binding.actionBar.flashButton.setImageResource(R.drawable.ic_flash_on)
                    }
                }
            }

            R.id.close_button -> {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        val allPermissionGranted = permission.all { it.value }

        if (allPermissionGranted) {
            showsUi(true)
            setUpViews()
        } else {
            val permissionList = permission.filter { !it.value }.keys
            handlePermissionResult(permissionList)
        }
    }

    override fun onItemClick(detailUrl: String) {
        val action = ScanFragmentDirections.actionScanFragmentToDetailFragment(detailUrl)
        findNavController().navigate(action)
    }


}