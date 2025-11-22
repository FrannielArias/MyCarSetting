package edu.ucne.loginapi.domain.useCase

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.repository.MaintenanceHistoryRepository

class DeleteMaintenanceRecordUseCase(
    private val repository: MaintenanceHistoryRepository
) {
    suspend operator fun invoke(recordId: String): Resource<Unit> =
        repository.deleteRecord(recordId)
}
