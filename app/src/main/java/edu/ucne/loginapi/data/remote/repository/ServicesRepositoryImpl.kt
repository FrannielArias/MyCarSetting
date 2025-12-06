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

    private fun mapDtoToServiceItem(dto: NominatimPlaceDto, userLat: Double?, userLon: Double?, category: ServiceCategory?): ServiceItem {
        val lat = dto.lat?.toDoubleOrNull() ?: 0.0
        val lon = dto.lon?.toDoubleOrNull() ?: 0.0

        val distanceText = if (userLat != null && userLon != null) {
            val result = FloatArray(1)
            Location.distanceBetween(userLat, userLon, lat, lon, result)
            val meters = result[0]
            when {
                meters >= 1000f -> String.format("A %.1f km", meters / 1000f)
                else -> String.format("A %d m", meters.toInt())
            }
        } else {
            ""
        }

        val inferredName = dto.displayName ?: dto.address?.values?.firstOrNull() ?: "Sin nombre"

        val inferredCategory = category ?: inferCategoryFromDto(dto)

        val isOpen = true

        return ServiceItem(
            id = (dto.placeId ?: dto.osmId ?: System.currentTimeMillis()).toString(),
            name = inferredName,
            category = inferredCategory,
            description = dto.type ?: dto.klass ?: "",
            distanceText = distanceText,
            isOpen = isOpen,
            latitude = lat,
            longitude = lon
        )
    }

    private fun inferCategoryFromDto(dto: NominatimPlaceDto): ServiceCategory {
        val t = (dto.type ?: "").lowercase()
        val klass = (dto.klass ?: "").lowercase()
        val display = (dto.displayName ?: "").lowercase()

        return when {
            t.contains("garage") || t.contains("car_repair") || display.contains("taller") || display.contains("mecanic") || klass.contains("building") -> ServiceCategory.TALLER
            display.contains("lavad") || t.contains("car_wash") -> ServiceCategory.LAVADO
            display.contains("emerg") || display.contains("grua") -> ServiceCategory.EMERGENCIA
            else -> ServiceCategory.MANTENIMIENTO
        }
    }

    override suspend fun searchServices(
        query: String,
        limit: Int,
        userLat: Double?,
        userLon: Double?,
        category: ServiceCategory?
    ): Resource<List<ServiceItem>> {
        return try {
            val q = query.trim()
            val list = api.searchPlaces(q, limit = limit)
            val items = list.map { dto ->
                mapDtoToServiceItem(dto, userLat, userLon, category)
            }
            val sorted = if (userLat != null && userLon != null) {
                items.sortedBy { item ->
                    val res = FloatArray(1)
                    Location.distanceBetween(userLat, userLon, item.latitude, item.longitude, res)
                    res[0]
                }
            } else items
            Resource.Success(sorted)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al consultar servicios")
        }
    }
}