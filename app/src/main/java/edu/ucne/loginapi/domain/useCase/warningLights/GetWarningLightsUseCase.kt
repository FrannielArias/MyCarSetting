package edu.ucne.loginapi.domain.useCase.warningLights

import edu.ucne.loginapi.domain.model.WarningLight
import edu.ucne.loginapi.domain.repository.ManualRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetWarningLightsUseCase @Inject constructor(
    private val repository: ManualRepository
) {
    operator fun invoke(
        brand: String? = null,
        model: String? = null,
        year: Int? = null
    ): Flow<List<WarningLight>> =
        repository.getWarningLights(brand, model, year)
}
