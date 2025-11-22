package edu.ucne.loginapi.domain.repository

import edu.ucne.loginapi.domain.model.GuideArticle
import edu.ucne.loginapi.domain.model.WarningLight
import kotlinx.coroutines.flow.Flow

interface ManualRepository {
    fun observeWarningLights(): Flow<List<WarningLight>>
    fun observeWarningLightById(id: String): Flow<WarningLight?>
    fun observeGuideArticles(): Flow<List<GuideArticle>>
    fun observeGuideArticleById(id: String): Flow<GuideArticle?>
}
