package com.example.sehatin.feature.profile

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sehatin.R
import com.example.sehatin.application.base.BaseFragment
import com.example.sehatin.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    private val viewModel: ProfileViewModel by viewModels()
    private val user : FirebaseUser? = Firebase.auth.currentUser
    private val db : FirebaseFirestore = Firebase.firestore


    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    override fun setUpViews() {
        super.setUpViews()
        user?.let {
            val username = it.displayName
            val email = it.email
            val photoUrl = it.photoUrl

            binding.apply {
                ivUsername.setImageURI(photoUrl)
                tvUsername.text = username
                tvEmail.text = email
            }
        }

    }



}