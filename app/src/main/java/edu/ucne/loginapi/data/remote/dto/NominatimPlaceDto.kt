package edu.ucne.loginapi.data.remote.dto

data class NominatimPlaceDto(
    val placeId: Long?,
    val osmType: String?,
    val osmId: Long?,
    val lat: String?,
    val lon: String?,
    val displayName: String?,
    val type: String?,
    val klass: String?,
    val address: Map<String, String>?,
    val extraTags: Map<String, String>?
)