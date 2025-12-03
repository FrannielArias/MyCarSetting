package edu.ucne.loginapi.domain.useCase

import edu.ucne.loginapi.domain.model.MaintenanceTask
import edu.ucne.loginapi.domain.repository.MaintenanceTaskRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveTasksForCarUseCase @Inject constructor(
    private val repository: MaintenanceTaskRepository
) {
    operator fun invoke(carId: Int): Flow<List<MaintenanceTask>> =
        repository.observeTasksForCar(carId)
}