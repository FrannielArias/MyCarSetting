package edu.ucne.loginapi.domain.useCase

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.MaintenanceTask
import edu.ucne.loginapi.domain.repository.MaintenanceTaskRepository

class UpdateMaintenanceTaskUseCase(
    private val repository: MaintenanceTaskRepository
) {
    suspend operator fun invoke(task: MaintenanceTask): Resource<MaintenanceTask> =
        repository.updateTaskLocal(task)
}
