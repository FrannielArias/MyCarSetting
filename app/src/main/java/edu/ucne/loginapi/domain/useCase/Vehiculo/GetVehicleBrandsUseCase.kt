package edu.ucne.loginapi.domain.useCase.Vehiculo

import edu.ucne.loginapi.domain.repository.VehicleCatalogRepository
import javax.inject.Inject

class GetVehicleBrandsUseCase @Inject constructor(
    private val repository: VehicleCatalogRepository
) {
    suspend operator fun invoke() = repository.getBrands()
}

class GetVehicleModelsUseCase @Inject constructor(
    private val repository: VehicleCatalogRepository
) {
    suspend operator fun invoke(brandId: String) = repository.getModels(brandId)
}

class GetVehicleYearRangesUseCase @Inject constructor(
    private val repository: VehicleCatalogRepository
) {
    suspend operator fun invoke(modelId: String) = repository.getYearRanges(modelId)
}
