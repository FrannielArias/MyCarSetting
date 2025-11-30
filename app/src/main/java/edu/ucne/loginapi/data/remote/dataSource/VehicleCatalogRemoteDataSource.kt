package edu.ucne.loginapi.data.remote.dataSource

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.data.remote.VehicleCatalogApiService
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
                val body = response.body().orEmpty()
                Resource.Success(body.map { it.toDomain() })
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR_MESSAGE)
        }
    }

    // ✅ Corregido: brandId es Int
    suspend fun getModelsByBrand(brandId: Int): Resource<List<VehicleModel>> {
        return try {
            val response = api.getModelsByBrand(brandId)
            if (response.isSuccessful) {
                val body = response.body().orEmpty()
                Resource.Success(body.map { it.toDomain() })
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR_MESSAGE)
        }
    }

    // ✅ Corregido: modelId es Int
    suspend fun getYearRangesByModel(modelId: Int): Resource<List<VehicleYearRange>> {
        return try {
            val response = api.getYearRangesByModel(modelId)
            if (response.isSuccessful) {
                val body = response.body().orEmpty()
                Resource.Success(body.map { it.toDomain() })
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR_MESSAGE)
        }
    }
}