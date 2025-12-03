package edu.ucne.loginapi.data.remote.dataSource

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.data.remote.VehicleCatalogApiService
import edu.ucne.loginapi.data.remote.dto.VehicleBrandDto
import edu.ucne.loginapi.data.remote.dto.VehicleModelDto
import edu.ucne.loginapi.data.remote.dto.VehicleYearRangeDto
import edu.ucne.loginapi.data.remote.mappers.toDomain
import edu.ucne.loginapi.domain.model.VehicleBrand
import edu.ucne.loginapi.domain.model.VehicleModel
import edu.ucne.loginapi.domain.model.VehicleYearRange
import javax.inject.Inject

class VehicleCatalogRemoteDataSource @Inject constructor(
    private val api: VehicleCatalogApiService
) {
    companion object {
        private const val NETWORK_ERROR_MESSAGE = "Error de red"
        private const val EMPTY_RESPONSE_MESSAGE = "Respuesta vacía"
    }

    suspend fun getBrands(): Resource<List<VehicleBrand>> {
        return try {
            val response = api.getBrands()
            if (response.isSuccessful) {
                val body: List<VehicleBrandDto> = response.body().orEmpty()
                val brands = body.map { dto: VehicleBrandDto -> dto.toDomain() }
                Resource.Success(brands)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR_MESSAGE)
        }
    }

    suspend fun getModelsByBrand(brandId: Int): Resource<List<VehicleModel>> {
        return try {
            val response = api.getModelsByBrand(brandId)
            if (response.isSuccessful) {
                val body: List<VehicleModelDto> = response.body().orEmpty()
                val models = body
                    .map { dto: VehicleModelDto -> dto.toDomain() }
                    .filter { model: VehicleModel -> model.brandId == brandId }

                Resource.Success(models)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR_MESSAGE)
        }
    }

    suspend fun getYearRangesByModel(modelId: Int): Resource<List<VehicleYearRange>> {
        return try {
            val response = api.getYearRangesByModel(modelId)
            if (response.isSuccessful) {
                val body: List<VehicleYearRangeDto> = response.body().orEmpty()
                val ranges = body
                    .map { dto: VehicleYearRangeDto -> dto.toDomain() }
                    .filter { range: VehicleYearRange -> range.modelId == modelId }

                Resource.Success(ranges)
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR_MESSAGE)
        }
    }
}