package edu.ucne.loginapi.domain.repository

import edu.ucne.loginapi.data.remote.Resource

interface MaintenanceRepository {
    suspend fun syncFromRemote(carId: Int): Resource<Unit>
    suspend fun pushPending(): Resource<Unit>

}
