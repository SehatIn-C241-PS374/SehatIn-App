package com.example.sehatin.application.data.response

import com.google.gson.annotations.SerializedName

data class ScanResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Nutrition(

	@field:SerializedName("carbohydrates")
	val carbohydrates: Float? = null,

	@field:SerializedName("fiber")
	val fiber: Float? = null,

	@field:SerializedName("calories")
	val calories: Float? = null,

)

data class Data(

	@field:SerializedName("item")
	val item: Item? = null
)

data class Item(

	@field:SerializedName("benefits")
	val benefits: List<String?>? = null,

	@field:SerializedName("nutrition")
	val nutrition: Nutrition? = null,

	@field:SerializedName("latin_name")
	val latinName: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null
)
