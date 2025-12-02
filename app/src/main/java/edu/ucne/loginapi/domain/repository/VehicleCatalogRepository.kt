package edu.ucne.loginapi.domain.repository

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.VehicleBrand
import edu.ucne.loginapi.domain.model.VehicleModel
import edu.ucne.loginapi.domain.model.VehicleYearRange
import kotlinx.coroutines.flow.Flow

interface VehicleCatalogRepository {
    fun getBrands(): Flow<List<VehicleBrand>>
    fun getModelsByBrand(brandId: Int): Flow<List<VehicleModel>>
    fun getYearRangesByModel(modelId: Int): Flow<List<VehicleYearRange>>
}