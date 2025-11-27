package edu.ucne.loginapi.domain.repository

import edu.ucne.loginapi.domain.model.GuideArticle
import edu.ucne.loginapi.domain.model.WarningLight
import kotlinx.coroutines.flow.Flow

interface ManualRepository {
    fun getWarningLights(
        brand: String?,
        model: String?,
        year: Int?
    ): Flow<List<WarningLight>>

    fun getWarningLightDetail(id: String): Flow<WarningLight?>

    fun getGuideArticles(category: String?): Flow<List<GuideArticle>>

    fun getGuideArticleDetail(id: String): Flow<GuideArticle?>
}
