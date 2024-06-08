package com.example.sehatin.feature.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sehatin.application.data.response.HitsItem
import com.example.sehatin.databinding.ItemRecipesBinding

class RecipesAdapter(private val recipes: List<HitsItem>,  private val listener: OnItemClickListener) : RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(private val binding: ItemRecipesBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: HitsItem , listener: OnItemClickListener) {
            binding.textViewTitle.text = recipe.recipe?.label ?: ""
            // Check if the image URL is not null before loading it with Glide
            recipe.recipe?.image?.let { imageUrl ->
                Glide.with(binding.root.context)
                    .load(imageUrl)
                    .into(binding.imageViewThumb)
            }
            binding.root.setOnClickListener{
                listener.onItemClick(recipe.recipe.url)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRecipesBinding.inflate(inflater, parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe,listener)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }
    interface OnItemClickListener {
        fun onItemClick(url: String)
    }
}
