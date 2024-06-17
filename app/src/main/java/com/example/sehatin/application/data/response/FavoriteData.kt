package com.example.sehatin.application.data.response

data class FavoriteData(
    val key: String = "",
    val imageUrl: String = "",
    val label: String = "",
    val source: String = "",
    val portion: Float = 0f,
    val time: Float = 0f,
    val calories: Float = 0f
)