package edu.ucne.loginapi.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.loginapi.data.entity.MaintenanceHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceHistoryDao {

    @Query("SELECT * FROM maintenance_history WHERE carId = :carId ORDER BY serviceDateMillis DESC")
    fun observeHistoryForCar(carId: String): Flow<List<MaintenanceHistoryEntity>>

    @Query("SELECT * FROM maintenance_history WHERE id = :id LIMIT 1")
    suspend fun getHistoryById(id: String): MaintenanceHistoryEntity?

    @Upsert
    suspend fun upsert(history: MaintenanceHistoryEntity)

    @Upsert
    suspend fun upsertAll(history: List<MaintenanceHistoryEntity>)

    @Query("DELETE FROM maintenance_history WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM maintenance_history WHERE carId = :carId")
    suspend fun clearForCar(carId: String)
}