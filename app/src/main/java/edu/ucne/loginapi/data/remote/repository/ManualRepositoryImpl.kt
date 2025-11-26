package edu.ucne.loginapi.data.repository

import edu.ucne.loginapi.data.remote.dataSource.ManualRemoteDataSource
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.GuideArticle
import edu.ucne.loginapi.domain.model.WarningLight
import edu.ucne.loginapi.domain.repository.ManualRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ManualRepositoryImpl @Inject constructor(
    private val remote: ManualRemoteDataSource
) : ManualRepository {

    override fun getWarningLights(
        brand: String?,
        model: String?,
        year: Int?
    ): Flow<List<WarningLight>> = flow {
        when (val result = remote.getWarningLights(brand, model, year)) {
            is Resource.Success -> emit(result.data.orEmpty())
            is Resource.Error, is Resource.Loading -> emit(emptyList())
        }
    }

    override fun getWarningLightDetail(id: String): Flow<WarningLight?> = flow {
        when (val result = remote.getWarningLightDetail(id)) {
            is Resource.Success -> emit(result.data)
            else -> emit(null)
        }
    }

    override fun getGuideArticles(category: String?): Flow<List<GuideArticle>> = flow {
        when (val result = remote.getGuideArticles(category)) {
            is Resource.Success -> emit(result.data.orEmpty())
            is Resource.Error, is Resource.Loading -> emit(emptyList())
        }
    }

    override fun getGuideArticleDetail(id: String): Flow<GuideArticle?> = flow {
        when (val result = remote.getGuideArticleDetail(id)) {
            is Resource.Success -> emit(result.data)
            else -> emit(null)
        }
    }
}
