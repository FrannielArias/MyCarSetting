package edu.ucne.loginapi.presentation.manual

import edu.ucne.loginapi.domain.model.GuideArticle
import edu.ucne.loginapi.domain.model.WarningLight

data class ManualUiState(
    val isLoading: Boolean = true,
    val selectedTabIndex: Int = 0,
    val warningLights: List<WarningLight> = emptyList(),
    val selectedWarningLight: WarningLight? = null,
    val guideArticles: List<GuideArticle> = emptyList(),
    val selectedArticle: GuideArticle? = null,
    val userMessage: String? = null
)