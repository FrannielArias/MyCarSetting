package edu.ucne.loginapi.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import edu.ucne.loginapi.data.entity.UserCarEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserCarDao {
    @Query("SELECT * FROM user_cars")
    fun getCars(): Flow<List<UserCarEntity>>

    @Query("SELECT * FROM user_cars WHERE id = :id LIMIT 1")
    suspend fun getCar(id: Int): UserCarEntity?

    @Query("SELECT * FROM user_cars WHERE isCurrent = 1 LIMIT 1")
    suspend fun getCurrentCar(): UserCarEntity?

    @Upsert
    suspend fun upsert(car: UserCarEntity)

    @Query("DELETE FROM user_cars WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("UPDATE user_cars SET isCurrent = 0")
    suspend fun clearCurrent()

    @Query("UPDATE user_cars SET isCurrent = 1 WHERE id = :id")
    suspend fun setCurrent(id: Int)

    @Query("DELETE FROM user_cars")
    suspend fun clearAll()

    @Query("SELECT * FROM user_cars WHERE pendingSync = 1")
    suspend fun getPendingSync(): List<UserCarEntity>

    @Transaction
    suspend fun replaceAll(cars: List<UserCarEntity>) {
        clearAll()
        cars.forEach { upsert(it) }
    }

}
