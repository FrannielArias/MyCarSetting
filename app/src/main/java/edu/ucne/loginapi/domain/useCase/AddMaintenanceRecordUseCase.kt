package edu.ucne.loginapi.domain.useCase

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.MaintenanceHistory
import edu.ucne.loginapi.domain.repository.MaintenanceHistoryRepository

class AddMaintenanceRecordUseCase(
    private val repository: MaintenanceHistoryRepository
) {
    suspend operator fun invoke(record: MaintenanceHistory): Resource<MaintenanceHistory> =
        repository.addRecord(record)
}
