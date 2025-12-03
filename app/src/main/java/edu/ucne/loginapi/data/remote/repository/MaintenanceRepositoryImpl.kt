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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MaintenanceRepositoryImpl @Inject constructor(
    private val taskDao: MaintenanceTaskDao,
    private val historyDao: MaintenanceHistoryDao,
    private val remote: MaintenanceRemoteDataSource
) : MaintenanceRepository, MaintenanceTaskRepository, MaintenanceHistoryRepository {

    override fun observeTasksForCar(carId: Int): Flow<List<MaintenanceTask>> =
        taskDao.observeTasksForCar(carId).map { list -> list.map { it.toDomain() } }

    override fun observeUpcomingTasksForCar(carId: Int): Flow<List<MaintenanceTask>> =
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

    override suspend fun getTaskById(id: Int): MaintenanceTask? =
        taskDao.getTaskById(id)?.toDomain()

    override suspend fun createTaskLocal(task: MaintenanceTask): Resource<MaintenanceTask> {
        val entity = task.toEntity().copy(
            isPendingCreate = true,
            isPendingUpdate = false,
            isPendingDelete = false
        )
        taskDao.upsert(entity)
        return Resource.Success(task)
    }

    override suspend fun updateTaskLocal(task: MaintenanceTask): Resource<MaintenanceTask> {
        val entity = task.toEntity().copy(
            isPendingUpdate = true,
            isPendingCreate = false
        )
        taskDao.upsert(entity)
        return Resource.Success(task)
    }

    override suspend fun deleteTaskLocal(id: Int): Resource<Unit> {
        val entity = taskDao.getTaskById(id)?.copy(
            isPendingDelete = true
        )
        if (entity != null) {
            taskDao.upsert(entity)
        }
        return Resource.Success(Unit)
    }

    override suspend fun markTaskCompleted(
        taskId: Int,
        completionDateMillis: Long
    ): Resource<Unit> {
        val task = getTaskById(taskId) ?: return Resource.Error("La tarea no existe")

        val updated = task.copy(
            status = MaintenanceStatus.COMPLETED,
            updatedAtMillis = completionDateMillis,
            isPendingUpdate = true
        )

        taskDao.upsert(updated.toEntity())

        val historyRecord = MaintenanceHistory(
            id = 0,
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

    override fun observeHistoryForCar(carId: Int): Flow<List<MaintenanceHistory>> =
        historyDao.observeHistoryForCar(carId).map { list -> list.map { it.toDomain() } }

    override suspend fun getHistoryById(id: Int): MaintenanceHistory? =
        historyDao.getHistoryById(id)?.toDomain()

    override suspend fun addRecord(record: MaintenanceHistory): Resource<MaintenanceHistory> {
        historyDao.upsert(record.toEntity())
        return Resource.Success(record)
    }

    override suspend fun deleteRecord(id: Int): Resource<Unit> {
        historyDao.delete(id)
        return Resource.Success(Unit)
    }

    override suspend fun syncFromRemote(carId: Int): Resource<Unit> {
        val tasksRes = remote.getTasksForCar(carId)
        if (tasksRes is Resource.Error) {
            return Resource.Error(tasksRes.message ?: "Error al obtener tareas")
        }

        val historyRes = remote.getHistoryForCar(carId)
        if (historyRes is Resource.Error) {
            return Resource.Error(historyRes.message ?: "Error al obtener historial")
        }

        val tasks = (tasksRes as Resource.Success).data.orEmpty()
        val history = (historyRes as Resource.Success).data.orEmpty()

        taskDao.clearForCar(carId)
        historyDao.clearForCar(carId)

        taskDao.upsertAll(tasks.map { it.toEntity().copy(
            isPendingCreate = false,
            isPendingUpdate = false,
            isPendingDelete = false
        )})

        historyDao.upsertAll(history.map { it.toEntity() })

        return Resource.Success(Unit)
    }

    override suspend fun pushPending(): Resource<Unit> = postPendingTasks()

    override suspend fun postPendingTasks(): Resource<Unit> {
        val creates = taskDao.getPendingCreateTasks()
        for (entity in creates) {
            val domain = entity.toDomain()
            val remoteResult = remote.createTask(domain)

            if (remoteResult is Resource.Success) {
                val created = remoteResult.data ?: continue
                val merged = created.toEntity().copy(
                    id = entity.id,
                    isPendingCreate = false
                )
                taskDao.upsert(merged)
            }
        }

        val updates = taskDao.getPendingUpdateTasks()
        for (entity in updates) {
            val domain = entity.toDomain()
            remote.updateTask(domain)
            taskDao.upsert(entity.copy(isPendingUpdate = false))
        }

        val deletes = taskDao.getPendingDeleteTasks()
        for (entity in deletes) {
            entity.remoteId?.let { remote.deleteTask(it) }
            taskDao.deleteTask(entity.id)
        }

        return Resource.Success(Unit)
    }
}
