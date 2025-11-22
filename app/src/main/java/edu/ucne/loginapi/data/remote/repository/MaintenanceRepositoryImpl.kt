package edu.ucne.loginapi.data.repository

import edu.ucne.loginapi.data.local.dao.MaintenanceHistoryDao
import edu.ucne.loginapi.data.local.dao.MaintenanceTaskDao
import edu.ucne.loginapi.data.mapper.toDomain
import edu.ucne.loginapi.data.mapper.toEntity
import edu.ucne.loginapi.data.remote.MaintenanceRemoteDataSource
import edu.ucne.loginapi.domain.model.MaintenanceHistory
import edu.ucne.loginapi.domain.model.MaintenanceTask
import edu.ucne.loginapi.domain.model.Resource
import edu.ucne.loginapi.domain.repository.MaintenanceRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MaintenanceRepositoryImpl @Inject constructor(
    private val taskDao: MaintenanceTaskDao,
    private val historyDao: MaintenanceHistoryDao,
    private val remote: MaintenanceRemoteDataSource
) : MaintenanceRepository {

    override fun observeTasksForCar(carId: String): Flow<List<MaintenanceTask>> =
        taskDao.observeTasksForCar(carId).map { list -> list.map { it.toDomain() } }

    override fun observeUpcomingTasks(carId: String): Flow<List<MaintenanceTask>> =
        taskDao.observeUpcomingTasks(carId).map { list -> list.map { it.toDomain() } }

    override fun observeOverdueTasks(carId: String): Flow<List<MaintenanceTask>> =
        taskDao.observeOverdueTasks(carId).map { list -> list.map { it.toDomain() } }

    override fun observeHistoryForCar(carId: String): Flow<List<MaintenanceHistory>> =
        historyDao.observeHistoryForCar(carId).map { list -> list.map { it.toDomain() } }

    override suspend fun upsertTaskLocal(task: MaintenanceTask): Resource<Unit> {
        taskDao.upsert(task.toEntity())
        return Resource.Success(Unit)
    }

    override suspend fun deleteTaskLocal(id: String): Resource<Unit> {
        taskDao.markPendingDelete(id)
        return Resource.Success(Unit)
    }

    override suspend fun addHistoryLocal(history: MaintenanceHistory): Resource<Unit> {
        historyDao.upsert(history.toEntity())
        return Resource.Success(Unit)
    }

    override suspend fun deleteHistoryLocal(id: String): Resource<Unit> {
        historyDao.markPendingDelete(id)
        return Resource.Success(Unit)
    }

    override suspend fun syncFromRemote(carId: String): Resource<Unit> {
        val tasksResult = remote.getTasksForCar(carId)
        val historyResult = remote.getHistoryForCar(carId)

        if (tasksResult is Resource.Error) return tasksResult
        if (historyResult is Resource.Error) return historyResult

        val tasks = tasksResult.data.orEmpty()
        val history = historyResult.data.orEmpty()

        taskDao.replaceAllForCar(carId, tasks.map { it.toEntity() })
        historyDao.replaceAllForCar(carId, history.map { it.toEntity() })

        return Resource.Success(Unit)
    }

    override suspend fun pushPending(): Resource<Unit> {
        val pendingCreates = taskDao.getPendingCreates()
        val pendingUpdates = taskDao.getPendingUpdates()
        val pendingDeletes = taskDao.getPendingDeletes()
        val historyPendingCreates = historyDao.getPendingCreates()
        val historyPendingDeletes = historyDao.getPendingDeletes()

        for (entity in pendingCreates) {
            val domain = entity.toDomain()
            when (val result = remote.createTask(domain)) {
                is Resource.Success -> {
                    val remoteTask = result.data ?: continue
                    taskDao.markAsSyncedCreate(entity.id, remoteTask.id)
                }
                is Resource.Error -> return Resource.Error("Error sync tareas")
                else -> {}
            }
        }

        for (entity in pendingUpdates) {
            val domain = entity.toDomain()
            when (val result = remote.updateTask(domain)) {
                is Resource.Success -> {
                    taskDao.markAsSyncedUpdate(entity.id)
                }
                is Resource.Error -> return Resource.Error("Error sync tareas")
                else -> {}
            }
        }

        for (entity in pendingDeletes) {
            val remoteId = entity.remoteId ?: continue
            when (val result = remote.deleteTask(remoteId)) {
                is Resource.Success -> {
                    taskDao.finalDelete(entity.id)
                }
                is Resource.Error -> return Resource.Error("Error sync tareas")
                else -> {}
            }
        }

        for (entity in historyPendingCreates) {
            val domain = entity.toDomain()
            when (val result = remote.createHistory(domain)) {
                is Resource.Success -> {
                    historyDao.markAsSyncedCreate(entity.id)
                }
                is Resource.Error -> return Resource.Error("Error sync historial")
                else -> {}
            }
        }

        for (entity in historyPendingDeletes) {
            val remoteId = entity.remoteId ?: continue
            when (val result = remote.deleteHistory(remoteId)) {
                is Resource.Success -> {
                    historyDao.finalDelete(entity.id)
                }
                is Resource.Error -> return Resource.Error("Error sync historial")
                else -> {}
            }
        }

        return Resource.Success(Unit)
    }
}
