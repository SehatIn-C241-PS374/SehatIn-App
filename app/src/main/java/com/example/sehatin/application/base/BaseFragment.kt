package com.example.sehatin.application.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import java.lang.IllegalStateException

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    private var _binding : VB? = null
    private val binding : VB
        get() = _binding ?: throw IllegalStateException("Binding not initialized")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getViewBinding(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    abstract fun getViewBinding (
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ) : VB

    open fun setUpViews() {}
    open fun setupObserver() {}



}