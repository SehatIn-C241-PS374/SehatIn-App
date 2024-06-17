package com.example.sehatin.feature.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.viewModels
import com.example.sehatin.R
import com.example.sehatin.application.base.BaseFragment
import com.example.sehatin.databinding.FragmentHomeBinding
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.sehatin.application.data.base.ApiResponse
import com.example.sehatin.feature.adapter.RecipesAdapter
import com.example.sehatin.utils.Toaster
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(), RecipesAdapter.OnItemClickListener {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    /**
     * Ensure the data is being fetch only one
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.fetchRecipe()
        }
    }
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun setupNavigation() {
        super.setupNavigation()

        auth = Firebase.auth
        if (auth.currentUser == null) {
            findNavController().navigate(R.id.auth)
        }

        val user = auth.currentUser
        user?.providerData.let{ userList ->
            userList?.forEach {
                binding.apply {
                    ivProfile.load(it.photoUrl)
                    tvUsername.text = getString(R.string.home_profile_header, it.displayName)
                }

            }
        }
    }

    override fun setUpViews() {
        super.setUpViews()

        binding.fabScan.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_scanFragment)
        }
    }

    override fun setupObserver() {
        super.setupObserver()

        viewModel.recipesData.observe(viewLifecycleOwner) { recipesResponse ->
            when (recipesResponse) {
                is ApiResponse.Success -> {
                    binding.loadingIndicator.hide()
                    binding.rvRecipe.visibility = View.VISIBLE
                    val recipeAdapter = RecipesAdapter(recipesResponse.data.item, this)
                    binding.rvRecipe.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvRecipe.adapter = recipeAdapter
                }

                is ApiResponse.Error -> {
                    binding.loadingIndicator.hide()
                    Toaster.show(requireContext(), recipesResponse.error)
                }

                ApiResponse.Loading -> {
                    binding.loadingIndicator.show()
                }
            }
        }
    }

    override fun onItemClick(detailUrl: String) {
        val action = HomeFragmentDirections.actionHomeFragmentToFragmentDetail(detailUrl)
        findNavController().navigate(action)
    }

}
