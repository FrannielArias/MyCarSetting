package edu.ucne.loginapi.data.remote.repository

import ServiceCategory
import ServiceItem
import android.location.Location
import android.util.Log
import edu.ucne.loginapi.data.remote.OverpassApiService
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.repository.ServicesRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class ServicesRepositoryImpl @Inject constructor(
    private val overpassApi: OverpassApiService
) : ServicesRepository {

    companion object {
        private const val TAG = "ServicesRepository"
    }

    override suspend fun searchServices(
        query: String,
        limit: Int,
        userLat: Double?,
        userLon: Double?,
        category: ServiceCategory?
    ): Resource<List<ServiceItem>> {

        Log.d(TAG, "🔍 searchServices llamado")
        Log.d(TAG, "📍 userLat: $userLat, userLon: $userLon")
        Log.d(TAG, "🏷️ category: $category")

        if (userLat == null || userLon == null) {
            Log.e(TAG, "❌ Ubicación no disponible")
            return Resource.Error("Ubicación no disponible")
        }

        return try {
            // Respetar rate limit
            delay(1000)

            // Construir query según categoría
            val overpassQuery = if (category == null) {
                Log.d(TAG, "🔄 Buscando TODOS los tipos de servicios...")
                buildMultiAmenityQuery(userLat, userLon)
            } else {
                val amenity = when (category) {
                    ServiceCategory.TALLER -> "car_repair"
                    ServiceCategory.GASOLINERA -> "fuel"
                    ServiceCategory.LAVADO -> "car_wash"
                    ServiceCategory.EMERGENCIA -> "emergency"
                    ServiceCategory.MANTENIMIENTO -> "car_repair"
                }
                Log.d(TAG, "🔄 Buscando amenity: $amenity")
                buildOverpassQuery(userLat, userLon, amenity)
            }

            Log.d(TAG, "📤 Query Overpass:")
            Log.d(TAG, overpassQuery)

            val response = overpassApi.executeQuery(overpassQuery)

            if (response.isSuccessful) {
                val elements = response.body()?.elements ?: emptyList()
                Log.d(TAG, "✅ Respuesta exitosa: ${elements.size} elementos")

                val items = elements.mapNotNull { element ->
                    mapElementToServiceItem(element, userLat, userLon, category)
                }
                Log.d(TAG, "✅ Items mapeados: ${items.size}")

                // Ordenar por distancia
                val sorted = items.sortedBy { item ->
                    val result = FloatArray(1)
                    Location.distanceBetween(userLat, userLon, item.latitude, item.longitude, result)
                    result[0]
                }

                // Filtrar solo los que están dentro de 10km
                val nearbyItems = sorted.filter { item ->
                    val result = FloatArray(1)
                    Location.distanceBetween(userLat, userLon, item.latitude, item.longitude, result)
                    result[0] <= 10_000f
                }

                Log.d(TAG, "✅ Items dentro de 10km: ${nearbyItems.size}")
                nearbyItems.forEach { item ->
                    Log.d(TAG, "  📍 ${item.name} - ${item.distanceText}")
                }

                Resource.Success(nearbyItems)
            } else {
                val errorMsg = "HTTP ${response.code()}: ${response.message()}"
                Log.e(TAG, "❌ Error HTTP: $errorMsg")
                Resource.Error(errorMsg)
            }

        } catch (e: Exception) {
            val errorMsg = e.localizedMessage ?: "Error al consultar servicios"
            Log.e(TAG, "❌ Excepción: $errorMsg", e)
            Resource.Error(errorMsg)
        }
    }

    private fun buildMultiAmenityQuery(lat: Double, lon: Double): String {
        return """
            [out:json][timeout:25];
            (
              node["amenity"="car_repair"](around:10000,$lat,$lon);
              node["amenity"="fuel"](around:10000,$lat,$lon);
              node["amenity"="car_wash"](around:10000,$lat,$lon);
              way["amenity"="car_repair"](around:10000,$lat,$lon);
              way["amenity"="fuel"](around:10000,$lat,$lon);
              way["amenity"="car_wash"](around:10000,$lat,$lon);
            );
            out center;
        """.trimIndent()
    }

    private fun buildOverpassQuery(lat: Double, lon: Double, amenity: String): String {
        return """
            [out:json][timeout:25];
            (
              node["amenity"="$amenity"](around:10000,$lat,$lon);
              way["amenity"="$amenity"](around:10000,$lat,$lon);
            );
            out center;
        """.trimIndent()
    }

    private fun mapElementToServiceItem(
        element: edu.ucne.loginapi.data.remote.dto.OverpassElement,
        userLat: Double,
        userLon: Double,
        requestedCategory: ServiceCategory?
    ): ServiceItem? {

        val lat = element.lat ?: element.center?.lat
        val lon = element.lon ?: element.center?.lon

        if (lat == null || lon == null) {
            Log.w(TAG, "⚠️ Elemento sin coordenadas: ${element.id}")
            return null
        }

        // Calcular distancia
        val result = FloatArray(1)
        Location.distanceBetween(userLat, userLon, lat, lon, result)
        val meters = result[0]

        val distanceText = if (meters >= 1000f) {
            "A %.1f km".format(meters / 1000f)
        } else {
            "A ${meters.toInt()} m"
        }

        val name = element.tags?.get("name")
            ?: element.tags?.get("operator")
            ?: element.tags?.get("brand")
            ?: "Sin nombre"

        val amenity = element.tags?.get("amenity") ?: ""
        val category = requestedCategory ?: inferCategoryFromAmenity(amenity)

        val description = buildDescription(element.tags, amenity)

        val isOpen = element.tags?.get("opening_hours")?.let { hours ->
            !hours.contains("closed", ignoreCase = true)
        } ?: true

        return ServiceItem(
            id = element.id.toString(),
            name = name,
            category = category,
            description = description,
            distanceText = distanceText,
            isOpen = isOpen,
            latitude = lat,
            longitude = lon
        )
    }

    private fun inferCategoryFromAmenity(amenity: String): ServiceCategory {
        return when (amenity.lowercase()) {
            "car_repair", "garage" -> ServiceCategory.TALLER
            "fuel" -> ServiceCategory.GASOLINERA
            "car_wash" -> ServiceCategory.LAVADO
            "emergency" -> ServiceCategory.EMERGENCIA
            else -> ServiceCategory.MANTENIMIENTO
        }
    }

    private fun buildDescription(tags: Map<String, String>?, amenity: String): String {
        if (tags == null) return ""

        val parts = mutableListOf<String>()

        when (amenity) {
            "car_repair" -> parts.add("Taller mecánico")
            "fuel" -> parts.add("Gasolinera")
            "car_wash" -> parts.add("Lavado de autos")
            "emergency" -> parts.add("Servicio de emergencia")
        }

        val street = tags["addr:street"]
        val houseNumber = tags["addr:housenumber"]
        if (street != null) {
            val address = if (houseNumber != null) "$street $houseNumber" else street
            parts.add(address)
        }

        return parts.joinToString(" • ")
    }

}