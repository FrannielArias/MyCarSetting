package edu.ucne.loginapi.domain.repository

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.VehicleBrand
import edu.ucne.loginapi.domain.model.VehicleModel
import edu.ucne.loginapi.domain.model.VehicleYearRange

interface VehicleCatalogRepository {
    suspend fun getBrands(): Resource<List<VehicleBrand>>
    suspend fun getModels(brandId: String): Resource<List<VehicleModel>>
    suspend fun getYearRanges(modelId: String): Resource<List<VehicleYearRange>>
}
