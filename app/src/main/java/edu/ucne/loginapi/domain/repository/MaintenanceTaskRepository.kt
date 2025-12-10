package edu.ucne.loginapi.domain.repository

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.MaintenanceTask
import kotlinx.coroutines.flow.Flow

interface MaintenanceTaskRepository {
    fun observeTasksForCar(carId: Int): Flow<List<MaintenanceTask>>
    fun observeUpcomingTasksForCar(carId: Int): Flow<List<MaintenanceTask>>
    fun observeOverdueTasksForCar(carId: Int): Flow<List<MaintenanceTask>>
    suspend fun getTaskById(id: Int): MaintenanceTask?
    suspend fun createTaskLocal(task: MaintenanceTask): Resource<MaintenanceTask>
    suspend fun updateTaskLocal(task: MaintenanceTask): Resource<MaintenanceTask>

    suspend fun markTaskCompleted(
        taskId: Int,
        completionDateMillis: Long,
        costAmount: Double? = null
    ): Resource<Unit>

    suspend fun deleteTaskLocal(id: Int): Resource<Unit>
    suspend fun postPendingTasks(): Resource<Unit>
}