package edu.ucne.loginapi.domain.useCase

import edu.ucne.loginapi.domain.model.WarningLight
import edu.ucne.loginapi.domain.repository.ManualRepository
import kotlinx.coroutines.flow.Flow

class GetWarningLightsUseCase(
    private val repository: ManualRepository
) {
    operator fun invoke(): Flow<List<WarningLight>> =
        repository.observeWarningLights()
}
