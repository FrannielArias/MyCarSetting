package edu.ucne.loginapi.domain.useCase

import edu.ucne.loginapi.domain.model.GuideArticle
import edu.ucne.loginapi.domain.repository.ManualRepository
import kotlinx.coroutines.flow.Flow

class GetGuideArticleDetailUseCase(
    private val repository: ManualRepository
) {
    operator fun invoke(id: String): Flow<GuideArticle?> =
        repository.observeGuideArticleById(id)
}
