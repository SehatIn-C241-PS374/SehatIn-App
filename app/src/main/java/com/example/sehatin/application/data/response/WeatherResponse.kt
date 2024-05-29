package com.example.sehatin.application.data.response

data class WeatherResponse(
	val current: Current,
	val location: Location
)

data class Condition(
	val code: Int,
	val icon: String,
	val text: String
)

data class Location(
	val localtime: String,
	val country: String,
	val localtimeEpoch: Int,
	val name: String,
	val lon: Any,
	val region: String,
	val lat: Any,
	val tzId: String
)

data class Current(
	val feelslikeC: Any,
	val feelslikeF: Any,
	val windDegree: Int,
	val windchillF: Any,
	val windchillC: Any,
	val lastUpdatedEpoch: Int,
	val tempC: Any,
	val tempF: Any,
	val cloud: Int,
	val windKph: Any,
	val windMph: Any,
	val humidity: Int,
	val dewpointF: Any,
	val uv: Any,
	val lastUpdated: String,
	val heatindexF: Any,
	val dewpointC: Any,
	val isDay: Int,
	val precipIn: Any,
	val heatindexC: Any,
	val windDir: String,
	val gustMph: Any,
	val pressureIn: Any,
	val gustKph: Any,
	val precipMm: Any,
	val condition: Condition,
	val visKm: Any,
	val pressureMb: Any,
	val visMiles: Any
)

