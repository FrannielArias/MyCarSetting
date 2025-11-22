package edu.ucne.loginapi.domain.useCase

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.repository.MaintenanceTaskRepository

class DeleteMaintenanceTaskUseCase(
    private val repository: MaintenanceTaskRepository
) {
    suspend operator fun invoke(taskId: String): Resource<Unit> =
        repository.deleteTaskLocal(taskId)
}
