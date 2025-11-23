package edu.ucne.loginapi.data

import edu.ucne.loginapi.data.dao.MaintenanceTaskDao
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.data.remote.mappers.toDomain
import edu.ucne.loginapi.data.remote.mappers.toEntity
import edu.ucne.loginapi.domain.model.MaintenanceStatus
import edu.ucne.loginapi.domain.model.MaintenanceTask
import edu.ucne.loginapi.domain.repository.MaintenanceTaskRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MaintenanceTaskRepositoryImpl @Inject constructor(
    private val maintenanceTaskDao: MaintenanceTaskDao
) : MaintenanceTaskRepository {

    override fun observeTasksForCar(carId: String): Flow<List<MaintenanceTask>> {
        return maintenanceTaskDao.observeTasksForCar(carId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeUpcomingTasksForCar(carId: String): Flow<List<MaintenanceTask>> {
        return observeTasksForCar(carId).map { tasks ->
            tasks.filter { it.status == MaintenanceStatus.UPCOMING }
        }
    }

    override fun observeOverdueTasksForCar(carId: String): Flow<List<MaintenanceTask>> {
        return observeTasksForCar(carId).map { tasks ->
            tasks.filter { it.status == MaintenanceStatus.OVERDUE }
        }
    }

    override suspend fun getTaskById(id: String): MaintenanceTask? {
        return maintenanceTaskDao.getTaskById(id)?.toDomain()
    }

    override suspend fun createTaskLocal(task: MaintenanceTask): Resource<MaintenanceTask> {
        return try {
            val pending = task.copy(
                isPendingCreate = true,
                isPendingUpdate = false,
                isPendingDelete = false
            )
            maintenanceTaskDao.upsert(pending.toEntity())
            Resource.Success(pending)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al crear tarea", task)
        }
    }

    override suspend fun updateTaskLocal(task: MaintenanceTask): Resource<MaintenanceTask> {
        return try {
            val pending = task.copy(
                isPendingUpdate = true,
                isPendingDelete = false
            )
            maintenanceTaskDao.upsert(pending.toEntity())
            Resource.Success(pending)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al actualizar tarea", task)
        }
    }

    override suspend fun markTaskCompleted(taskId: String, completionDateMillis: Long): Resource<Unit> {
        return try {
            val entity = maintenanceTaskDao.getTaskById(taskId) ?: return Resource.Error("Tarea no encontrada")
            val domain = entity.toDomain()
            val updated = domain.copy(
                status = MaintenanceStatus.COMPLETED,
                isPendingUpdate = true
            )
            maintenanceTaskDao.upsert(updated.toEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al completar tarea")
        }
    }

    override suspend fun deleteTaskLocal(id: String): Resource<Unit> {
        return try {
            val entity = maintenanceTaskDao.getTaskById(id)
            if (entity == null) {
                return Resource.Error("Tarea no encontrada")
            }
            if (entity.remoteId == null) {
                maintenanceTaskDao.deleteTask(id)
            } else {
                val updated = entity.copy(
                    isPendingDelete = true,
                    isPendingCreate = false,
                    isPendingUpdate = false
                )
                maintenanceTaskDao.upsert(updated)
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al eliminar tarea")
        }
    }

    override suspend fun postPendingTasks(): Resource<Unit> {
        return Resource.Success(Unit)
    }
}
