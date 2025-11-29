package edu.ucne.loginapi.data.remote.repository

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.data.remote.dataSource.VehicleCatalogRemoteDataSource
import edu.ucne.loginapi.domain.model.VehicleBrand
import edu.ucne.loginapi.domain.model.VehicleModel
import edu.ucne.loginapi.domain.model.VehicleYearRange
import edu.ucne.loginapi.domain.repository.VehicleCatalogRepository
import javax.inject.Inject

class VehicleCatalogRepositoryImpl @Inject constructor(
    private val remote: VehicleCatalogRemoteDataSource
) : VehicleCatalogRepository {

    override suspend fun getBrands(): Resource<List<VehicleBrand>> =
        remote.getBrands()

    override suspend fun getModels(brandId: String): Resource<List<VehicleModel>> =
        remote.getModels(brandId)

    override suspend fun getYearRanges(modelId: String): Resource<List<VehicleYearRange>> =
        remote.getYearRanges(modelId)
}
