package edu.ucne.loginapi.data.remote.dataSource

import edu.ucne.loginapi.data.mapper.toDomain
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.data.remote.apiService.ManualApiService
import edu.ucne.loginapi.domain.model.GuideArticle
import edu.ucne.loginapi.domain.model.WarningLight
import javax.inject.Inject

class ManualRemoteDataSource @Inject constructor(
    private val api: ManualApiService
) {
    companion object {
        private const val NETWORK_ERROR_MESSAGE = "Network error"
        private const val EMPTY_RESPONSE_MESSAGE = "Empty response"
    }

    suspend fun getWarningLights(
        brand: String?,
        model: String?,
        year: Int?
    ): Resource<List<WarningLight>> {
        return try {
            val response = api.getWarningLights(brand, model, year)
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

    suspend fun getWarningLightDetail(id: String): Resource<WarningLight> {
        return try {
            val response = api.getWarningLightDetail(id)
            if (response.isSuccessful) {
                val body = response.body() ?: return Resource.Error(EMPTY_RESPONSE_MESSAGE)
                Resource.Success(body.toDomain())
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR_MESSAGE)
        }
    }

    suspend fun getGuideArticles(category: String?): Resource<List<GuideArticle>> {
        return try {
            val response = api.getGuideArticles(category)
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

    suspend fun getGuideArticleDetail(id: String): Resource<GuideArticle> {
        return try {
            val response = api.getGuideArticleDetail(id)
            if (response.isSuccessful) {
                val body = response.body() ?: return Resource.Error(EMPTY_RESPONSE_MESSAGE)
                Resource.Success(body.toDomain())
            } else {
                Resource.Error("HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: NETWORK_ERROR_MESSAGE)
        }
    }
}
