package edu.ucne.loginapi.domain.useCase

import edu.ucne.loginapi.domain.model.MaintenanceTask
import edu.ucne.loginapi.domain.repository.MaintenanceTaskRepository
import kotlinx.coroutines.flow.Flow

class ObserveTasksForCarUseCase(
    private val repository: MaintenanceTaskRepository
) {
    operator fun invoke(carId: String): Flow<List<MaintenanceTask>> =
        repository.observeTasksForCar(carId)
}
