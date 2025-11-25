package edu.ucne.loginapi.domain.useCase

import edu.ucne.loginapi.domain.model.WarningLight
import edu.ucne.loginapi.domain.repository.ManualRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetWarningLightDetailUseCase @Inject constructor(
    private val repository: ManualRepository
) {
    operator fun invoke(id: String): Flow<WarningLight?> =
        repository.getWarningLightDetail(id)
}
