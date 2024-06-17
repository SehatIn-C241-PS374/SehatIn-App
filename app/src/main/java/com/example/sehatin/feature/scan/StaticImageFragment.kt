package com.example.sehatin.feature.scan

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sehatin.R
import com.example.sehatin.application.base.BaseFragment
import com.example.sehatin.application.data.base.ApiResponse
import com.example.sehatin.application.data.response.HistoryData
import com.example.sehatin.databinding.FragmentStaticImageBinding
import com.example.sehatin.feature.adapter.RecipesAdapter
import com.example.sehatin.utils.ImageClassifierHelper
import com.example.sehatin.utils.Toaster
import com.example.sehatin.utils.formatCurrentDate
import com.example.sehatin.utils.formatEnglishLabel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import org.tensorflow.lite.support.label.Category
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Setup the Chip Text
 * Working on the Helper and make a viewModel out of this
 * */

@AndroidEntryPoint
class StaticImageFragment : BaseFragment<FragmentStaticImageBinding>(), OnClickListener,
    RecipesAdapter.OnItemClickListener {

    private val viewModel: ScanViewModel by viewModels()
    private var currentImageUri: Uri? = null
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var imageClassifierHelper: ImageClassifierHelper? = null
    private var user : FirebaseUser? = null
    private var db : FirebaseFirestore = Firebase.firestore
    private var userRef : DocumentReference? = null
    private var isDetected : Boolean = true
    private lateinit var historyData : HistoryData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.setWorkflowState(ScanViewModel.WorkflowState.CONFIRMING)
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentStaticImageBinding {
        return FragmentStaticImageBinding.inflate(inflater, container, false)
    }

    override fun onResume() {
        super.onResume()
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun setUpViews() {
        super.setUpViews()

        user = Firebase.auth.currentUser
        user?.let {
            userRef  = db.collection("users").document(user?.uid!!)
        }

        binding.ivPreview.setImageURI(currentImageUri)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomNav.bottomNavView)
        bottomSheetBehavior?.peekHeight = 128
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior?.isHideable = false

        binding.topActionBar.galleryButton.setOnClickListener(this)
        binding.topActionBar.closeButton.setOnClickListener(this)
        binding.bottomSheetScrimView.setOnClickListener(this)

        setupBottomNav()
        onBackHandler()

    }


    override fun setupObserver() {
        super.setupObserver()

        viewModel.workflowState.observe(viewLifecycleOwner) { workFlowState ->

            when (workFlowState) {
                ScanViewModel.WorkflowState.CONFIRMING -> {
                    binding.bottomPromptChip.text = "Pick the image to classify"
                }

                ScanViewModel.WorkflowState.SEARCHED -> {

                    binding.bottomPromptChip.text = "Wait for a moment"
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val imageSource = ImageDecoder.createSource(
                            requireContext().contentResolver,
                            currentImageUri!!
                        )
                        ImageDecoder.decodeBitmap(imageSource)
                    } else {
                        MediaStore.Images.Media.getBitmap(
                            requireContext().contentResolver,
                            currentImageUri!!
                        )
                    }.copy(Bitmap.Config.ARGB_8888, true)?.let { bitmap ->
                        analyzeImage(bitmap)
                    }


                }

                ScanViewModel.WorkflowState.DETECTED -> {
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
                                }

                                if (isDetected) {
                                    historyData = HistoryData(
                                        formatCurrentDate(),
                                        data?.name!!,
                                        data.nutrition?.calories!!,
                                        data.nutrition.carbohydrates!!,
                                        data.nutrition.fiber!!
                                    )

                                    userRef?.collection("history")?.add(historyData)
                                        ?.addOnSuccessListener {}
                                }

                            }

                            is ApiResponse.Error -> {
                                Toaster.show(requireContext(), "Someting was off")
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
                                binding.bottomNav.rvRecomendationDish.layoutManager = LinearLayoutManager(requireContext())
                                binding.bottomNav.rvRecomendationDish.adapter = recipeAdapter

                                binding.loadingIndicator.hide()
                                isDetected = false
                                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                                binding.bottomSheetScrimView.invalidate()
                                binding.bottomNav.bottomNavView.visibility = View.VISIBLE
                                binding.bottomPromptChip.text = "Here's your result"

                            }

                            is ApiResponse.Error -> {
                                Toaster.show(requireContext(), "Something was off")
                            }

                            ApiResponse.Loading -> {}
                        }
                    }


                }

                else -> {}

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
                            viewModel.setWorkflowState(ScanViewModel.WorkflowState.DETECTED)
                        }

                    }
                }

            })

        imageClassifierHelper?.classifyImage(bitmap)

    }

    private fun setupBottomNav() {
        bottomSheetBehavior?.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomView: View, newState: Int) {
                    binding.bottomSheetScrimView.visibility =
                        if (newState == BottomSheetBehavior.STATE_COLLAPSED) View.GONE
                        else View.VISIBLE

                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    binding.bottomSheetScrimView.invalidate()
                }

            }
        )
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
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
            viewModel.setWorkflowState(ScanViewModel.WorkflowState.SEARCHED)
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

    override fun onItemClick(detailUrl: String) {
        val action = StaticImageFragmentDirections.actionStaticImageFragmentToDetailFragment(detailUrl)
        findNavController().navigate(action)
    }

    override fun onClick(view: View?) {
        when (view?.id) {

            R.id.gallery_button ->{
                isDetected = true
                galleryLauncher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )

            }
            R.id.close_button -> requireActivity().onBackPressedDispatcher.onBackPressed()
            R.id.bottom_sheet_scrim_view -> bottomSheetBehavior?.state =
                BottomSheetBehavior.STATE_COLLAPSED

        }
    }


}