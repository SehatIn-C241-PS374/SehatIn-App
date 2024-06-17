package com.example.sehatin.feature.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.sehatin.R
import com.example.sehatin.application.data.response.FavoriteData
import com.example.sehatin.application.data.response.RecipeItem
import com.example.sehatin.databinding.ItemRecipesBinding
import com.example.sehatin.utils.parseIdFromUrl
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class FavoriteAdapter(
    options: FirestoreRecyclerOptions<FavoriteData>
) : FirestoreRecyclerAdapter<FavoriteData, FavoriteAdapter.FavoriteViewHolder>(options) {

    inner class FavoriteViewHolder(
        private val binding: ItemRecipesBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: FavoriteData) {
            with(binding) {
                tvTitle.text = recipe.label
                tvSource.text = recipe.source
                ivRecipe.load(recipe.imageUrl) {
                    crossfade(true)
                    placeholder(R.color.grey)
                    transformations(CircleCropTransformation())
                }
                tvPortion.text =
                    context.getString(R.string.item_recipes_portion, recipe.portion.toInt())
                tvTime.text =
                    context.getString(R.string.item_recipe_minute, recipe.time.toInt())
                tvCalorie.text =
                    context.getString(R.string.item_recipe_calorie, recipe.calories.toInt())

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemRecipesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int, model: FavoriteData) {
        val data = getItem(position)
        holder.bind(data)
    }

}