package edu.ucne.loginapi.domain.useCase.maintenance

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.repository.MaintenanceHistoryRepository
import jakarta.inject.Inject

class DeleteMaintenanceRecordUseCase @Inject constructor(
    private val repository: MaintenanceHistoryRepository
) {
    suspend operator fun invoke(recordId: String): Resource<Unit> =
        repository.deleteRecord(recordId)
}
