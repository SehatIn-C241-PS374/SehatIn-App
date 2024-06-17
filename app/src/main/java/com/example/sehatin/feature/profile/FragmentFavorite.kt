package com.example.sehatin.feature.profile

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sehatin.application.base.BaseFragment
import com.example.sehatin.application.data.response.FavoriteData
import com.example.sehatin.databinding.FragmentFavoriteBinding
import com.example.sehatin.feature.adapter.FavoriteAdapter
import com.example.sehatin.utils.Toaster
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FragmentFavorite : BaseFragment<FragmentFavoriteBinding>() {

    private val user: FirebaseUser? = Firebase.auth.currentUser
    private val db: FirebaseFirestore = Firebase.firestore
    private var adapter: FavoriteAdapter? = null
    private val userRef = db.collection("users").document(user?.uid!!)

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentFavoriteBinding {
        return FragmentFavoriteBinding.inflate(inflater, container, false)
    }

    override fun setUpViews() {
        super.setUpViews()

        val query: Query = userRef
            .collection("favorites")

        val options = FirestoreRecyclerOptions.Builder<FavoriteData>()
            .setQuery(query, FavoriteData::class.java)
            .build()

        adapter = FavoriteAdapter(options)
        binding.rvFavorite.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorite.adapter = adapter

    }

    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (adapter != null) {
            adapter!!.stopListening()
        }
    }

}