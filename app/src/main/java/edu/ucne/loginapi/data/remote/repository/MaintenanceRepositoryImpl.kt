package edu.ucne.loginapi.data.remote.repository

import edu.ucne.loginapi.data.dao.MaintenanceHistoryDao
import edu.ucne.loginapi.data.dao.MaintenanceTaskDao
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.data.remote.dataSource.MaintenanceRemoteDataSource
import edu.ucne.loginapi.data.remote.mappers.toDomain
import edu.ucne.loginapi.data.remote.mappers.toEntity
import edu.ucne.loginapi.domain.model.MaintenanceHistory
import edu.ucne.loginapi.domain.model.MaintenanceStatus
import edu.ucne.loginapi.domain.model.MaintenanceTask
import edu.ucne.loginapi.domain.model.MaintenanceType
import edu.ucne.loginapi.domain.repository.MaintenanceHistoryRepository
import edu.ucne.loginapi.domain.repository.MaintenanceRepository
import edu.ucne.loginapi.domain.repository.MaintenanceTaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class MaintenanceRepositoryImpl @Inject constructor(
    private val taskDao: MaintenanceTaskDao,
    private val historyDao: MaintenanceHistoryDao,
    private val remote: MaintenanceRemoteDataSource
) : MaintenanceRepository, MaintenanceTaskRepository, MaintenanceHistoryRepository {

    override fun observeTasksForCar(carId: String): Flow<List<MaintenanceTask>> =
        taskDao.observeTasksForCar(carId).map { list -> list.map { it.toDomain() } }

    override fun observeUpcomingTasksForCar(carId: String): Flow<List<MaintenanceTask>> =
        observeTasksForCar(carId)

    override fun observeOverdueTasksForCar(carId: String): Flow<List<MaintenanceTask>> =
        observeTasksForCar(carId)

    override suspend fun getTaskById(id: String): MaintenanceTask? =
        taskDao.getTaskById(id)?.toDomain()

    override suspend fun createTaskLocal(task: MaintenanceTask): Resource<MaintenanceTask> {
        val existing = taskDao.getTaskById(task.id)
        return if (existing == null) {
            taskDao.upsert(task.toEntity())
            Resource.Success(task)
        } else {
            Resource.Error("La tarea ya existe")
        }
    }

    override suspend fun updateTaskLocal(task: MaintenanceTask): Resource<MaintenanceTask> {
        val existing = taskDao.getTaskById(task.id)
        return if (existing != null) {
            taskDao.upsert(task.toEntity())
            Resource.Success(task)
        } else {
            Resource.Error("La tarea no existe")
        }
    }

    override suspend fun markTaskCompleted(
        taskId: String,
        completionDateMillis: Long
    ): Resource<Unit> {
        return try {
            val taskEntity = taskDao.getTaskById(taskId)
                ?: return Resource.Error("Tarea no encontrada")

            val updatedTask = taskEntity.copy(
                status = MaintenanceStatus.COMPLETED.name,
                updatedAtMillis = completionDateMillis
            )
            taskDao.upsert(updatedTask)

            val historyRecord = MaintenanceHistory(
                id = UUID.randomUUID().toString(),
                carId = taskEntity.carId,
                taskType = MaintenanceType.valueOf(taskEntity.type),
                serviceDateMillis = completionDateMillis,
                mileageKm = taskEntity.dueMileageKm,
                workshopName = null,
                cost = null,
                notes = taskEntity.description
            )

            historyDao.upsert(historyRecord.toEntity())

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al completar tarea")
        }
    }

    override suspend fun deleteTaskLocal(id: String): Resource<Unit> {
        taskDao.deleteTask(id)
        return Resource.Success(Unit)
    }

    override suspend fun postPendingTasks(): Resource<Unit> {
        return Resource.Success(Unit)
    }

    override fun observeHistoryForCar(carId: String): Flow<List<MaintenanceHistory>> =
        historyDao.observeHistoryForCar(carId).map { list -> list.map { it.toDomain() } }

    override suspend fun getHistoryById(id: String): MaintenanceHistory? =
        historyDao.getHistoryById(id)?.toDomain()

    override suspend fun addRecord(record: MaintenanceHistory): Resource<MaintenanceHistory> {
        return try {
            historyDao.upsert(record.toEntity())
            Resource.Success(record)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al guardar historial", record)
        }
    }

    override suspend fun deleteRecord(id: String): Resource<Unit> {
        return try {
            historyDao.delete(id)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al eliminar historial")
        }
    }


    override suspend fun syncFromRemote(carId: String): Resource<Unit> {
        return Resource.Success(Unit)
    }

    override suspend fun pushPending(): Resource<Unit> {
        return Resource.Success(Unit)
    }
}
