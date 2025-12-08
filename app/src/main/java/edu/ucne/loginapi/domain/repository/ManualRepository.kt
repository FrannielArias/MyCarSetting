package edu.ucne.loginapi.domain.repository

import edu.ucne.loginapi.domain.model.GuideArticle
import edu.ucne.loginapi.domain.model.WarningLight
import kotlinx.coroutines.flow.Flow

interface ManualRepository {
    fun getWarningLights(): Flow<List<WarningLight>>
    fun getWarningLightDetail(id: Int): Flow<WarningLight?>
    fun getGuideArticles(category: String?): Flow<List<GuideArticle>>
    fun getGuideArticleDetail(id: Int): Flow<GuideArticle?>

}