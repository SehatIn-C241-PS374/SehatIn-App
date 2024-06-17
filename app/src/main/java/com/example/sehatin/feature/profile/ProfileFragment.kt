package com.example.sehatin.feature.profile

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.sehatin.R
import com.example.sehatin.application.base.BaseFragment
import com.example.sehatin.databinding.FragmentProfileBinding
import com.example.sehatin.feature.adapter.ViewStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    private val user : FirebaseUser? = Firebase.auth.currentUser


    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    override fun setUpViews() {
        super.setUpViews()
        user?.providerData.let{ userList ->
            userList?.forEach {
                binding.layoutHeader.apply {
                    ivProfilePic.load(it.photoUrl)
                    tvName.text = getString(R.string.home_profile_header, it.displayName)
                    tvEmail.text = it.email
                }

            }
        }


        setupMenu()
        setupViewPager()

    }

    private fun setupMenu() {
        binding.topAppBar.setOnMenuItemClickListener {menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    Firebase.auth.signOut()
                    findNavController().navigate(R.id.auth)
                    true
                }
                else -> {true}

            }
        }
    }

    private fun setupViewPager() {
        binding.viewPager.adapter = ViewStateAdapter(childFragmentManager, lifecycle)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text =
                if (position == 0) "Favorite" else "History"
        }.attach()
    }




}