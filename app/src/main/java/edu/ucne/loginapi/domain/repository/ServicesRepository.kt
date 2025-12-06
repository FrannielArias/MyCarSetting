package edu.ucne.loginapi.domain.repository

import ServiceCategory
import ServiceItem
import edu.ucne.loginapi.data.remote.Resource

interface ServicesRepository {
    suspend fun searchServices(
        query: String,
        limit: Int = 30,
        userLat: Double? = null,
        userLon: Double? = null,
        category: ServiceCategory? = null
    ): Resource<List<ServiceItem>>
}