package edu.ucne.loginapi.data

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.MaintenanceHistory
import edu.ucne.loginapi.domain.repository.MaintenanceHistoryRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MaintenanceHistoryRepositoryImpl @Inject constructor(
    private val maintenanceHistoryDao: MaintenanceHistoryDao
) : MaintenanceHistoryRepository {

    override fun observeHistoryForCar(carId: String): Flow<List<MaintenanceHistory>> {
        return maintenanceHistoryDao.observeHistoryForCar(carId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getHistoryById(id: String): MaintenanceHistory? {
        return maintenanceHistoryDao.getHistoryById(id)?.toDomain()
    }

    override suspend fun addRecord(record: MaintenanceHistory): Resource<MaintenanceHistory> {
        return try {
            maintenanceHistoryDao.insert(record.toEntity())
            Resource.Success(record)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al guardar historial", record)
        }
    }

    override suspend fun deleteRecord(id: String): Resource<Unit> {
        return try {
            maintenanceHistoryDao.delete(id)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al eliminar historial")
        }
    }
}
