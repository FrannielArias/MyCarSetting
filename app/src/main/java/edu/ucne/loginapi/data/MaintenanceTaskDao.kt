package edu.ucne.loginapi.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceTaskDao {

    @Query("SELECT * FROM maintenance_tasks WHERE carId = :carId")
    fun observeTasksForCar(carId: String): Flow<List<MaintenanceTaskEntity>>

    @Query("SELECT * FROM maintenance_tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: String): MaintenanceTaskEntity?

    @Upsert
    suspend fun upsert(task: MaintenanceTaskEntity)

    @Query("DELETE FROM maintenance_tasks WHERE id = :id")
    suspend fun deleteTask(id: String)

    @Query("SELECT * FROM maintenance_tasks WHERE isPendingCreate = 1")
    suspend fun getPendingCreateTasks(): List<MaintenanceTaskEntity>

    @Query("SELECT * FROM maintenance_tasks WHERE isPendingUpdate = 1")
    suspend fun getPendingUpdateTasks(): List<MaintenanceTaskEntity>

    @Query("SELECT * FROM maintenance_tasks WHERE isPendingDelete = 1")
    suspend fun getPendingDeleteTasks(): List<MaintenanceTaskEntity>

}