package com.example.sehatin.feature.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.sehatin.application.data.response.IngredientsItem
import com.example.sehatin.databinding.ItemIngredientsBinding
import com.example.sehatin.utils.format
import kotlin.math.round

class IngredientsAdapter(
    private val ingredients: List<IngredientsItem>) : RecyclerView.Adapter<IngredientsAdapter.IngredientsAdapterViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IngredientsAdapterViewHolder {
        val binding =
            ItemIngredientsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IngredientsAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientsAdapterViewHolder, position: Int) {
        val item = ingredients[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }

    inner class IngredientsAdapterViewHolder(private val binding: ItemIngredientsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: IngredientsItem) {
            with(binding) {
                ivFood.load(item.image)
                tvFood.text = item.food
                tvMeasurement.text = item.quantity.format()
                tvUnits.text = item.measure
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(recipeId: String)
    }


}

