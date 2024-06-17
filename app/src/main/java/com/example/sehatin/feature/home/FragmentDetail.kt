package com.example.sehatin.feature.home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.Intent.CATEGORY_BROWSABLE
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.amulyakhare.textdrawable.TextDrawable
import com.example.sehatin.R
import com.example.sehatin.application.base.BaseFragment
import com.example.sehatin.application.data.base.ApiResponse
import com.example.sehatin.application.data.response.FavoriteData
import com.example.sehatin.application.data.response.IngredientsItem
import com.example.sehatin.application.data.response.TotalNutrients
import com.example.sehatin.databinding.FragmentDetailBinding
import com.example.sehatin.feature.adapter.IngredientsAdapter
import com.example.sehatin.utils.Colors
import com.example.sehatin.utils.FOOD_DESCRIPTION
import com.example.sehatin.utils.Toaster
import com.example.sehatin.utils.format
import com.example.sehatin.utils.getTextDrawable
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round


@AndroidEntryPoint
class FragmentDetail : BaseFragment<FragmentDetailBinding>(), View.OnClickListener {

    private val viewModel: FragmentDetailViewModel by viewModels()
    private lateinit var pieChart: PieChart
    private val args: FragmentDetailArgs by navArgs()
    private var user: FirebaseUser? = Firebase.auth.currentUser
    private var db: FirebaseFirestore = Firebase.firestore
    private var recipeId: String? = null
    private var userRef: DocumentReference = db.collection("users").document(user?.uid!!)
    private lateinit var favoriteData: FavoriteData
    private lateinit var pieData: PieData
    private var listenerRegistration: ListenerRegistration? = null

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentDetailBinding {
        return FragmentDetailBinding.inflate(inflater, container, false)
    }

    override fun setUpViews() {
        super.setUpViews()

        recipeId = args.recipeId
        viewModel.getDetailRecipe(recipeId!!)
        binding.fragmentDetail.visibility = View.INVISIBLE
        binding.loadingIndicator.show()

        updateRecipeFavorite()
        onBackHandler()

        binding.topActionBar.closeButton.setOnClickListener(this)
        binding.btnScan.setOnClickListener(this)


    }


    override fun setupObserver() {
        super.setupObserver()

        binding.loadingIndicator.show()
        viewModel.recipesData.observe(viewLifecycleOwner) { status ->
            when (status) {
                is ApiResponse.Success -> {
                    binding.loadingIndicator.hide()
                    val data = status.data.recipe

                    with(binding) {
                        ivFood.load(data.image)
                        tvTitle.text = data.label
                        tvDescription.text = FOOD_DESCRIPTION.random()
                        tvIngredients.text = data.ingredients.size.toString()
                        setupAdapter(data.ingredients)
                        setupNutrition(data.totalNutrients)
                        tvCaloriesValue.text = getString(
                            R.string.detail_calori_value,
                            data.totalNutrients.energyKcal.quantity
                        )

                        tvTotalFat.text =
                            getString(
                                R.string.detail_fat_value,
                                data.totalNutrients.fat.quantity
                            )

                        tvTotalCholesterol.text = getString(
                            R.string.detail_cholesterol_value,
                            data.totalNutrients.cholesterol.quantity
                        )
                        tvTotalProtein.text = getString(
                            R.string.detail_protein_value,
                            data.totalNutrients.protein.quantity
                        )

                        tvCreator.text = data.source

                        val textDrawable = getTextDrawable(
                            Colors.getRandomColor(),
                            TextDrawable.SHAPE_ROUND,
                            data.source.first().toString()
                        )
                        ivUsername.setImageDrawable(textDrawable)

                        cardCreator.setOnClickListener {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                try {
                                    val intent = Intent(ACTION_VIEW, Uri.parse(data.url)).apply {
                                        // The URL should either launch directly in a non-browser app
                                        // (if it’s the default), or in the disambiguation dialog
                                        addCategory(CATEGORY_BROWSABLE)
                                        flags = FLAG_ACTIVITY_NEW_TASK
                                    }
                                    startActivity(intent)
                                } catch (e: ActivityNotFoundException) {
                                    Toaster.show(
                                        requireContext(),
                                        "(API 11) No browser available to open the URL",
                                    )
                                }

                            } else {
                                val intent = Intent(ACTION_VIEW, Uri.parse(data.url))

                                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                                    startActivity(intent)
                                } else {
                                    Toaster.show(
                                        requireContext(),
                                        "No browser available to open the URL",
                                    )
                                }

                            }
                        }


                    }

                    favoriteData = FavoriteData(
                        args.recipeId,
                        data.image,
                        data.label,
                        data.source,
                        data.servings,
                        data.totalTime,
                        data.calories
                    )

                    binding.fragmentDetail.visibility = View.VISIBLE


                }

                is ApiResponse.Error -> {
                    binding.loadingIndicator.hide()
                    Toaster.show(requireContext(), status.error)
                }

                ApiResponse.Loading -> {
                    binding.loadingIndicator.show()
                }
            }
        }
    }

    private fun setupNutrition(totalNutrients: TotalNutrients) {
        pieChart = binding.pieChart
        val pieEntries = ArrayList<PieEntry>()
        val label = "type"

        val typeAmountMap: MutableMap<String, Float> = HashMap()
        typeAmountMap["Fat"] = totalNutrients.fat.quantity
        typeAmountMap["Carbs"] = totalNutrients.carbs.quantity
        typeAmountMap["Protein"] = totalNutrients.protein.quantity

        val colors = ArrayList<Int>()
        colors.add(Color.parseColor("#304567"))
        colors.add(Color.parseColor("#309967"))
        colors.add(Color.parseColor("#476567"))

        for (type in typeAmountMap.keys) {
            pieEntries.add(PieEntry(typeAmountMap[type]!!, type))
        }

        val pieDataSet = PieDataSet(pieEntries, label)
        pieDataSet.valueTextSize = 12f
        pieDataSet.colors = colors
        pieDataSet.valueFormatter = PercentFormatter()

        pieData = PieData(pieDataSet)
        pieChart.setData(pieData)
        pieChart.invalidate()
    }

    private fun setupAdapter(ingredients: List<IngredientsItem>) {
        val adapter = IngredientsAdapter(ingredients)
        binding.rvIngredient.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rvIngredient.adapter = adapter
    }


    private fun onBackHandler() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.close_button -> requireActivity().onBackPressedDispatcher.onBackPressed()
            R.id.btnScan -> findNavController().navigate(R.id.scanFragment)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove()
    }

    private fun updateRecipeFavorite() {
        val favoriteButtonStatus =
            userRef.collection("favorites").whereEqualTo("key", recipeId!!).limit(1)
        listenerRegistration = favoriteButtonStatus.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("Detail", "Error listening for favorite status", error)
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                binding.topActionBar.favoriteButton.setImageResource(R.drawable.ic_favorite)
                binding.topActionBar.favoriteButton.setOnClickListener {
                    userRef.collection("favorites").document(recipeId!!).delete()
                        .addOnSuccessListener {
                            Toaster.show(
                                requireContext(),
                                getString(R.string.detail_success_remote_favorite)
                            )
                        }
                        .addOnFailureListener {
                            Toaster.show(
                                requireContext(),
                                getString(R.string.detail_failed_update_favorites)
                            )
                        }
                }
            } else {
                binding.topActionBar.favoriteButton.setImageResource(R.drawable.ic_unfavorite)
                binding.topActionBar.favoriteButton.setOnClickListener {
                    userRef.collection("favorites").document(recipeId!!).set(favoriteData)
                        .addOnSuccessListener {
                            Toaster.show(
                                requireContext(),
                                getString(R.string.detail_success_add_favorite)
                            )
                        }
                        .addOnFailureListener {
                            Toaster.show(
                                requireContext(),
                                getString(R.string.detail_failed_update_favorites)
                            )
                        }
                }

            }

        }

    }

}

