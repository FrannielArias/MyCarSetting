package edu.ucne.loginapi.domain.repository

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.MaintenanceHistory
import kotlinx.coroutines.flow.Flow

interface MaintenanceHistoryRepository {
    fun observeHistoryForCar(carId: Int): Flow<List<MaintenanceHistory>>
    suspend fun getHistoryById(id: Int): MaintenanceHistory?
    suspend fun addRecord(record: MaintenanceHistory): Resource<MaintenanceHistory>
    suspend fun deleteRecord(id: Int): Resource<Unit>
}