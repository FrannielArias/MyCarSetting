package edu.ucne.loginapi.domain.useCase.Vehiculo

import edu.ucne.loginapi.domain.model.VehicleBrand
import edu.ucne.loginapi.domain.model.VehicleModel
import edu.ucne.loginapi.domain.model.VehicleYearRange
import edu.ucne.loginapi.domain.repository.VehicleCatalogRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVehicleBrandsUseCase @Inject constructor(
    private val repository: VehicleCatalogRepository
) {
    operator fun invoke(): Flow<List<VehicleBrand>> =
        repository.getBrands()
}

class GetVehicleModelsByBrandUseCase @Inject constructor(
    private val repository: VehicleCatalogRepository
) {
    operator fun invoke(brandId: Int): Flow<List<VehicleModel>> =
        repository.getModelsByBrand(brandId)
}

class GetVehicleYearRangesByModelUseCase @Inject constructor(
    private val repository: VehicleCatalogRepository
) {
    operator fun invoke(modelId: Int): Flow<List<VehicleYearRange>> =
        repository.getYearRangesByModel(modelId)
}
