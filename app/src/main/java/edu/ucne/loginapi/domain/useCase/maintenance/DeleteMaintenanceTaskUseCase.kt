package edu.ucne.loginapi.domain.useCase.maintenance

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.repository.MaintenanceTaskRepository
import jakarta.inject.Inject

class DeleteMaintenanceTaskUseCase @Inject constructor(
    private val repository: MaintenanceTaskRepository
) {
    suspend operator fun invoke(taskId: String): Resource<Unit> =
        repository.deleteTaskLocal(taskId)
}
