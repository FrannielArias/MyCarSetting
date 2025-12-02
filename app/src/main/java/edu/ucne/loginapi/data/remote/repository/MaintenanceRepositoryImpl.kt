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
import edu.ucne.loginapi.domain.repository.MaintenanceHistoryRepository
import edu.ucne.loginapi.domain.repository.MaintenanceRepository
import edu.ucne.loginapi.domain.repository.MaintenanceTaskRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MaintenanceRepositoryImpl @Inject constructor(
    private val taskDao: MaintenanceTaskDao,
    private val historyDao: MaintenanceHistoryDao,
    private val remote: MaintenanceRemoteDataSource
) : MaintenanceRepository, MaintenanceTaskRepository, MaintenanceHistoryRepository {

    fun observeTasksForCar(carId: Int): Flow<List<MaintenanceTask>> =
        taskDao.observeTasksForCar(carId).map { list -> list.map { it.toDomain() } }

    override fun observeUpcomingTasksForCar(carId: String): Flow<List<MaintenanceTask>> =
        observeTasksForCar(carId).map { tasks ->
            val now = System.currentTimeMillis()
            tasks.filter { task ->
                task.status != MaintenanceStatus.COMPLETED &&
                        (task.dueDateMillis == null || task.dueDateMillis >= now)
            }
        }

    override fun observeOverdueTasksForCar(carId: Int): Flow<List<MaintenanceTask>> =
        observeTasksForCar(carId).map { tasks ->
            val now = System.currentTimeMillis()
            tasks.filter { task ->
                task.status != MaintenanceStatus.COMPLETED &&
                        task.dueDateMillis != null &&
                        task.dueDateMillis < now
            }
        }

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
        val task = getTaskById(taskId)
            ?: return Resource.Error("La tarea no existe")

        val updated = task.copy(
            status = MaintenanceStatus.COMPLETED,
            updatedAtMillis = completionDateMillis
        )
        taskDao.upsert(updated.toEntity())

        val historyRecord = MaintenanceHistory(
            carId = task.carId,
            taskType = task.type,
            serviceDateMillis = completionDateMillis,
            mileageKm = task.dueMileageKm,
            cost = null,
            workshopName = null,
            notes = task.title
        )
        historyDao.upsert(historyRecord.toEntity())

        return Resource.Success(Unit)
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
        historyDao.upsert(record.toEntity())
        return Resource.Success(record)
    }

    override suspend fun deleteRecord(id: String): Resource<Unit> {
        historyDao.delete(id)
        return Resource.Success(Unit)
    }

    override suspend fun syncFromRemote(carId: String): Resource<Unit> {
        return Resource.Success(Unit)
    }

    override suspend fun pushPending(): Resource<Unit> {
        return Resource.Success(Unit)
    }
}
