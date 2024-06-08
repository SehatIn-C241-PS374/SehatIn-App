package com.example.sehatin.feature.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.sehatin.R
import com.example.sehatin.application.base.BaseFragment
import com.example.sehatin.databinding.FragmentHomeBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.Firebase
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser
//import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private val db : FirebaseFirestore = com.google.firebase.ktx.Firebase.firestore
    private val userRef = db.collection("users")

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun setupNavigation() {
        auth = Firebase.auth
        if (auth.currentUser == null) {
            findNavController().navigate(R.id.auth)
        }
    }

    override fun setUpViews() {

        binding.fabScan.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_scanFragment)

        }

    }

    override fun setupObserver() {

    }


}