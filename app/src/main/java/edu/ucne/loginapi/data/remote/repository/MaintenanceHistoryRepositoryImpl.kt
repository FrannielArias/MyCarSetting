package edu.ucne.loginapi.data.remote.repository

import edu.ucne.loginapi.data.dao.MaintenanceHistoryDao
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.data.remote.mappers.toDomain
import edu.ucne.loginapi.data.remote.mappers.toEntity
import edu.ucne.loginapi.domain.model.MaintenanceHistory
import edu.ucne.loginapi.domain.repository.MaintenanceHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MaintenanceHistoryRepositoryImpl @Inject constructor(
    private val maintenanceHistoryDao: MaintenanceHistoryDao
) : MaintenanceHistoryRepository {

    override fun observeHistoryForCar(carId: Int): Flow<List<MaintenanceHistory>> {
        return maintenanceHistoryDao.observeHistoryForCar(carId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getHistoryById(id: Int): MaintenanceHistory? {
        return maintenanceHistoryDao.getHistoryById(id)?.toDomain()
    }

    override suspend fun addRecord(record: MaintenanceHistory): Resource<MaintenanceHistory> {
        return try {
            maintenanceHistoryDao.upsert(record.toEntity())
            Resource.Success(record)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al guardar historial", record)
        }
    }

    override suspend fun deleteRecord(id: Int): Resource<Unit> {
        return try {
            maintenanceHistoryDao.delete(id)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al eliminar historial")
        }
    }

}