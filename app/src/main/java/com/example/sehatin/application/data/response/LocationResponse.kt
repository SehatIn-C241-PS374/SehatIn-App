package com.example.sehatin.application.data.response

import com.google.gson.annotations.SerializedName

data class LocationResponse(

	@field:SerializedName("data")
	val data: List<DataItem>,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataItem(

	@field:SerializedName("address")
	val address: String,

	@field:SerializedName("distance")
	val distance: Int,

	@field:SerializedName("latitude")
	val latitude: Double,

	@field:SerializedName("nama_toko")
	val namaToko: String,

	@field:SerializedName("longitude")
	val longitude: Double
)
