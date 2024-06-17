package com.example.sehatin.feature.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sehatin.R
import com.example.sehatin.application.data.response.HistoryData
import com.example.sehatin.databinding.ItemHistoryBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class HistoryAdapter(options: FirestoreRecyclerOptions<HistoryData>) :
    FirestoreRecyclerAdapter<HistoryData, HistoryAdapter.HistoryViewHolder>(options)
{
    inner class HistoryViewHolder(private val binding: ItemHistoryBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HistoryData) {
            with(binding) {
                tvFood.text = item.name
                tvCalori.text = context.getString(R.string.detail_calori_value, item.calories)
                tvCarbs.text = context.getString(R.string.detail_protein_value, item.carbs)
                tvFiber.text = context.getString(R.string.detail_cholesterol_value, item.fiber)
                tvTimestamp.text = context.getString(R.string.history_date_scanned, item.timestamp)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int, model: HistoryData) {
        getItem(position).let {
            holder.bind(it)
        }
    }
}