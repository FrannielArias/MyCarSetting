package edu.ucne.loginapi.domain.useCase

import edu.ucne.loginapi.domain.model.MaintenanceHistory
import edu.ucne.loginapi.domain.repository.MaintenanceHistoryRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetMaintenanceHistoryForCarUseCase @Inject constructor(
    private val repository: MaintenanceHistoryRepository
) {
    operator fun invoke(carId: String): Flow<List<MaintenanceHistory>> =
        repository.observeHistoryForCar(carId)
}
