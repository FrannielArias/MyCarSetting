package edu.ucne.loginapi.domain.useCase

import edu.ucne.loginapi.domain.model.MaintenanceHistory
import edu.ucne.loginapi.domain.repository.MaintenanceHistoryRepository
import kotlinx.coroutines.flow.Flow

class GetMaintenanceHistoryForCarUseCase(
    private val repository: MaintenanceHistoryRepository
) {
    operator fun invoke(carId: String): Flow<List<MaintenanceHistory>> =
        repository.observeHistoryForCar(carId)
}
