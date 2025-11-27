package edu.ucne.loginapi.data.remote.apiService

import edu.ucne.loginapi.data.remote.dto.GuideArticleDto
import edu.ucne.loginapi.data.remote.dto.WarningLightDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ManualApiService {
    @GET("api/manual/warningLights")
    suspend fun getWarningLights(
        @Query("brand") brand: String?,
        @Query("model") model: String?,
        @Query("year") year: Int?
    ): Response<List<WarningLightDto>>

    @GET("api/manual/warningLights/{id}")
    suspend fun getWarningLightDetail(
        @Path("id") id: String
    ): Response<WarningLightDto>

    @GET("api/manual/guides")
    suspend fun getGuideArticles(
        @Query("category") category: String?
    ): Response<List<GuideArticleDto>>

    @GET("api/manual/guides/{id}")
    suspend fun getGuideArticleDetail(
        @Path("id") id: String
    ): Response<GuideArticleDto>
}
