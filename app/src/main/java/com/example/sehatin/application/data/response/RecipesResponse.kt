package com.example.sehatin.application.data.response

import com.google.gson.annotations.SerializedName

data class RecipesResponse(
	@field:SerializedName("hits")
	val item: List<RecipeItem>
)

data class RecipeItem(

	@field:SerializedName("_links")
	val links: Links,

	@field:SerializedName("recipe")
	val recipe: Recipe

)

data class Links(

	@field:SerializedName("self")
	val self: Self
)

data class Self(

	@field:SerializedName("href")
	val href: String

)

data class Recipe(

	@field:SerializedName("image")
	val image: String,

	@field:SerializedName("healthLabels")
	val healthLabels: List<String>,

	@field:SerializedName("mealType")
	val mealType: List<String>,

	@field:SerializedName("label")
	val label: String,

	@field:SerializedName("source")
	val source: String,

	@field:SerializedName("calories")
	val calories: Float,

	@field:SerializedName("url")
	val url: String,

	@field:SerializedName("totalTime")
	val totalTime: Float,

	@field:SerializedName("totalNutrients")
	val totalNutrients: TotalNutrients,

	@field:SerializedName("yield")
	val servings: Float,

	@field:SerializedName("totalWeight")
	val totalWeight: Float,

	@field:SerializedName("ingredients")
	val ingredients: List<IngredientsItem>
)

data class IngredientsItem(

	@field:SerializedName("image")
	val image: String,

	@field:SerializedName("quantity")
	val quantity: Float,

	@field:SerializedName("measure")
	val measure: String,

	@field:SerializedName("foodId")
	val foodId: String,

	@field:SerializedName("weight")
	val weight: Float,

	@field:SerializedName("text")
	val text: String,

	@field:SerializedName("foodCategory")
	val foodCategory: String,

	@field:SerializedName("food")
	val food: String
)

data class TotalNutrients(

	@field:SerializedName("ENERC_KCAL")
	val energyKcal: ENERCKCAL,

	@field:SerializedName("FAT")
	val fat: FAT,

	@field:SerializedName("CHOCDF")
	val carbs: CHOCDF,
	
	@field:SerializedName("VITC")
	val vitC: VITC,
	
	@field:SerializedName("K")
	val pottasium: K,

	@field:SerializedName("VITD")
	val vitD: VITD,

	@field:SerializedName("CHOLE")
	val cholesterol: CHOLE,

	@field:SerializedName("CA")
	val calcium: CA,

	@field:SerializedName("FIBTG")
	val fiber: FIBTG,

	@field:SerializedName("VITB12")
	val vitB12: VITB12,

	@field:SerializedName("SUGAR")
	val sugar: SUGAR,

	@field:SerializedName("PROCNT")
	val protein: PROCNT
)

data class VITB12(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Float,

	@field:SerializedName("label")
	val label: String
)




data class CHOLE(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Float,

	@field:SerializedName("label")
	val label: String
)

data class VITD(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Float,

	@field:SerializedName("label")
	val label: String
)


data class CA(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Float,

	@field:SerializedName("label")
	val label: String
)

data class PROCNT(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Float,

	@field:SerializedName("label")
	val label: String
)



data class FAT(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Float,

	@field:SerializedName("label")
	val label: String
)

data class ENERCKCAL(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Float,

	@field:SerializedName("label")
	val label: String
)


data class SUGAR(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Float,

	@field:SerializedName("label")
	val label: String
)

data class FIBTG(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Float,

	@field:SerializedName("label")
	val label: String
)


data class CHOCDF(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Float,

	@field:SerializedName("label")
	val label: String
)

data class K(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Float,

	@field:SerializedName("label")
	val label: String
)




data class VITC(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Float,

	@field:SerializedName("label")
	val label: String
)
