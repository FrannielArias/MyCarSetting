package edu.ucne.loginapi.data.repository

import edu.ucne.loginapi.data.dao.MaintenanceHistoryDao
import edu.ucne.loginapi.data.dao.MaintenanceTaskDao
import edu.ucne.loginapi.data.remote.dataSource.MaintenanceRemoteDataSource
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.data.remote.mappers.toDomain
import edu.ucne.loginapi.data.remote.mappers.toEntity
import edu.ucne.loginapi.domain.model.MaintenanceHistory
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

    override fun observeTasksForCar(carId: String): Flow<List<MaintenanceTask>> =
        taskDao.observeTasksForCar(carId).map { list -> list.map { it.toDomain() } }

    override fun observeUpcomingTasksForCar(carId: String): Flow<List<MaintenanceTask>> =
        observeTasksForCar(carId)

    override fun observeOverdueTasksForCar(carId: String): Flow<List<MaintenanceTask>> =
        observeTasksForCar(carId)

    override suspend fun getTaskById(id: String): MaintenanceTask? =
        taskDao.getTaskById(id)?.toDomain()

    override suspend fun createTaskLocal(task: MaintenanceTask): Resource<MaintenanceTask> {
        taskDao.upsert(task.toEntity())
        return Resource.Success(task)
    }

    override suspend fun updateTaskLocal(task: MaintenanceTask): Resource<MaintenanceTask> {
        taskDao.upsert(task.toEntity())
        return Resource.Success(task)
    }

    override suspend fun markTaskCompleted(taskId: String, completionDateMillis: Long): Resource<Unit> {
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
        historyDao.insert(record.toEntity())
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
