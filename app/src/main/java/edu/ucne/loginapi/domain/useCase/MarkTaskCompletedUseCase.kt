package edu.ucne.loginapi.domain.useCase

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.repository.MaintenanceTaskRepository

class MarkTaskCompletedUseCase(
    private val repository: MaintenanceTaskRepository
) {
    suspend operator fun invoke(taskId: String, completionDateMillis: Long): Resource<Unit> =
        repository.markTaskCompleted(taskId, completionDateMillis)
}
