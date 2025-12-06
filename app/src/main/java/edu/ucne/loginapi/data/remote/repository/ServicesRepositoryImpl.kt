package edu.ucne.loginapi.data.remote.repository

import ServiceCategory
import ServiceItem
import android.location.Location
import edu.ucne.loginapi.data.remote.NominatimApiService
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.data.remote.dto.NominatimPlaceDto
import edu.ucne.loginapi.domain.repository.ServicesRepository
import javax.inject.Inject
import kotlin.collections.map

class ServicesRepositoryImpl @Inject constructor(
    private val api: NominatimApiService
) : ServicesRepository {

    override suspend fun searchServices(
        query: String,
        limit: Int,
        userLat: Double?,
        userLon: Double?,
        category: ServiceCategory?
    ): Resource<List<ServiceItem>> {

        return try {
            val q = when (category) {
                ServiceCategory.GASOLINERA -> "amenity=fuel"
                ServiceCategory.TALLER -> "shop=car_repair OR amenity=garage"
                ServiceCategory.LAVADO -> "amenity=car_wash"
                ServiceCategory.EMERGENCIA -> "amenity=hospital OR emergency"
                else -> "amenity=fuel OR shop=car_repair OR amenity=car_wash"
            }

            val result = api.search(q = q, limit = limit)

            val mapped = result.mapNotNull { dto -> mapDto(dto, userLat, userLon) }

            Resource.Success(mapped)

        } catch (e: Exception) {
            Resource.Error("Error cargando servicios: ${e.message}")
        }
    }

    private fun mapDto(dto: NominatimPlaceDto, lat: Double?, lon: Double?): ServiceItem? {
        val latitude = dto.lat?.toDoubleOrNull() ?: return null
        val longitude = dto.lon?.toDoubleOrNull() ?: return null

        val distanceText =
            if (lat != null && lon != null) {
                val r = FloatArray(1)
                Location.distanceBetween(lat, lon, latitude, longitude, r)
                val m = r[0]
                if (m >= 1000) "A %.1f km".format(m / 1000) else "A ${m.toInt()} m"
            } else ""

        return ServiceItem(
            id = dto.placeId?.toString() ?: "${latitude}_${longitude}",
            name = dto.displayName ?: "Servicio",
            category = inferCategory(dto),
            description = dto.type ?: "",
            distanceText = distanceText,
            latitude = latitude,
            longitude = longitude,
            isOpen = dto.extraTags?.containsKey("opening_hours") == true
        )
    }

    private fun inferCategory(dto: NominatimPlaceDto): ServiceCategory {
        val t = dto.type?.lowercase() ?: ""
        val display = dto.displayName?.lowercase() ?: ""

        return when {
            t.contains("fuel") || display.contains("gas") -> ServiceCategory.GASOLINERA
            t.contains("car_repair") || display.contains("taller") -> ServiceCategory.TALLER
            t.contains("car_wash") || display.contains("lavado") -> ServiceCategory.LAVADO
            t.contains("hospital") || display.contains("emerg") -> ServiceCategory.EMERGENCIA
            else -> ServiceCategory.MANTENIMIENTO
        }
    }
}
