package edu.ucne.loginapi.domain.repository

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.MaintenanceTask
import kotlinx.coroutines.flow.Flow

interface MaintenanceTaskRepository {

    fun observeTasksForCar(carId: String): Flow<List<MaintenanceTask>>
    fun observeUpcomingTasksForCar(carId: String): Flow<List<MaintenanceTask>>
    fun observeOverdueTasksForCar(carId: String): Flow<List<MaintenanceTask>>
    suspend fun getTaskById(id: String): MaintenanceTask?
    suspend fun createTaskLocal(task: MaintenanceTask): Resource<MaintenanceTask>
    suspend fun updateTaskLocal(task: MaintenanceTask): Resource<MaintenanceTask>
    suspend fun markTaskCompleted(taskId: String, completionDateMillis: Long): Resource<Unit>
    suspend fun deleteTaskLocal(id: String): Resource<Unit>
    suspend fun postPendingTasks(): Resource<Unit>
}