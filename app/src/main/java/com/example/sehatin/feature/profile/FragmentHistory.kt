package com.example.sehatin.feature.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sehatin.application.base.BaseFragment
import com.example.sehatin.application.data.response.HistoryData
import com.example.sehatin.databinding.FragmentHistoryBinding
import com.example.sehatin.feature.adapter.HistoryAdapter
import com.example.sehatin.utils.Toaster
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class FragmentHistory : BaseFragment<FragmentHistoryBinding>() {

    private val user: FirebaseUser? = Firebase.auth.currentUser
    private val db: FirebaseFirestore = Firebase.firestore
    private var adapter : HistoryAdapter? = null

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentHistoryBinding {
        return FragmentHistoryBinding.inflate(inflater, container, false)
    }


    override fun setUpViews() {
        super.setUpViews()

        val userRef = db.collection("users").document(user?.uid!!)

        val query: Query = userRef
            .collection("history")
            .orderBy("timestamp")
            .limit(20)

        val options = FirestoreRecyclerOptions.Builder<HistoryData>()
            .setQuery(query, HistoryData::class.java)
            .build()

        adapter = HistoryAdapter(options)
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter

    }

    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }


    override fun onDestroy() {
        super.onDestroy()
        if (adapter != null) {
            adapter!!.stopListening()
        }
    }


}