package edu.ucne.loginapi.data.remote.repository

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.data.remote.dataSource.VehicleCatalogRemoteDataSource
import edu.ucne.loginapi.domain.model.VehicleBrand
import edu.ucne.loginapi.domain.model.VehicleModel
import edu.ucne.loginapi.domain.model.VehicleYearRange
import edu.ucne.loginapi.domain.repository.VehicleCatalogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class VehicleCatalogRepositoryImpl @Inject constructor(
    private val remote: VehicleCatalogRemoteDataSource
) : VehicleCatalogRepository {

    override fun getBrands(): Flow<List<VehicleBrand>> = flow {
        when (val result = remote.getBrands()) {
            is Resource.Success -> emit(result.data.orEmpty())
            is Resource.Error, is Resource.Loading -> emit(emptyList())
        }
    }

    override fun getModelsByBrand(brandId: Int): Flow<List<VehicleModel>> = flow {
        when (val result = remote.getModelsByBrand(brandId)) {
            is Resource.Success -> emit(result.data.orEmpty())
            is Resource.Error, is Resource.Loading -> emit(emptyList())
        }
    }

    override fun getYearRangesByModel(modelId: Int): Flow<List<VehicleYearRange>> = flow {
        when (val result = remote.getYearRangesByModel(modelId)) {
            is Resource.Success -> emit(result.data.orEmpty())
            is Resource.Error, is Resource.Loading -> emit(emptyList())
        }
    }
}