package com.example.sehatin.feature.adapter

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.sehatin.R
import com.example.sehatin.application.data.response.RecipeItem
import com.example.sehatin.databinding.ItemRecipesBinding
import com.example.sehatin.utils.parseIdFromUrl

class RecipesAdapter(
    private val recipes: List<RecipeItem>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder>() {
    inner class RecipeViewHolder(private val binding: ItemRecipesBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: RecipeItem, listener: OnItemClickListener) {
            with(binding) {
                tvTitle.text = recipe.recipe.label
                tvSource.text = recipe.recipe.source
                ivRecipe.load(recipe.recipe.image) {
                    crossfade(true)
                    placeholder(R.color.grey)
                    transformations(CircleCropTransformation())
                }
                tvPortion.text =
                    context.getString(R.string.item_recipes_portion, recipe.recipe.servings.toInt())
                tvTime.text =
                    context.getString(R.string.item_recipe_minute, recipe.recipe.totalTime.toInt())
                tvCalorie.text =
                    context.getString(R.string.item_recipe_calorie, recipe.recipe.calories.toInt())

                root.setOnClickListener {
                    val endpoint = parseIdFromUrl(recipe.links.self.href)
                    Log.d("Home", endpoint)
                    listener.onItemClick(endpoint)
                }
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRecipesBinding.inflate(inflater, parent, false)
        return RecipeViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe, listener)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    interface OnItemClickListener {
        fun onItemClick(detailUrl: String)
    }

}
