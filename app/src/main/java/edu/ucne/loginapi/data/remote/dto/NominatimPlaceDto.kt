package edu.ucne.loginapi.data.remote.dto

data class OverpassResponse(
    val version: Double,
    val elements: List<OverpassElement>
)

data class OverpassElement(
    val type: String,
    val id: Long,
    val lat: Double?,
    val lon: Double?,
    val tags: Map<String, String>?,
    val center: OverpassCenter?
)

data class OverpassCenter(
    val lat: Double,
    val lon: Double
)