package edu.ucne.loginapi.domain.useCase.guideArticle

import edu.ucne.loginapi.domain.model.GuideArticle
import edu.ucne.loginapi.domain.repository.ManualRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGuideArticlesUseCase @Inject constructor(
    private val repository: ManualRepository
) {
    operator fun invoke(category: String?): Flow<List<GuideArticle>> =
        repository.getGuideArticles(category)
}
