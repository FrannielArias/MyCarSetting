package edu.ucne.loginapi.domain.useCase.services


import ServiceCategory
import ServiceItem
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.repository.ServicesRepository
import javax.inject.Inject

class SearchServicesUseCase @Inject constructor(
    private val repository: ServicesRepository
) {
    suspend operator fun invoke(
        query: String,
        limit: Int = 30,
        userLat: Double? = null,
        userLon: Double? = null,
        category: ServiceCategory? = null
    ): Resource<List<ServiceItem>> = repository.searchServices(query, limit, userLat, userLon, category)
}