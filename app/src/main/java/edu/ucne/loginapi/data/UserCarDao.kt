package edu.ucne.loginapi.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserCarDao {

    @Query("SELECT * FROM user_cars")
    fun observeCars(): Flow<List<UserCarEntity>>

    @Query("SELECT * FROM user_cars WHERE isCurrent = 1 LIMIT 1")
    fun observeCurrentCar(): Flow<UserCarEntity?>

    @Query("SELECT * FROM user_cars WHERE isCurrent = 1 LIMIT 1")
    suspend fun getCurrentCar(): UserCarEntity?

    @Query("SELECT * FROM user_cars WHERE id = :id LIMIT 1")
    suspend fun getCarById(id: String): UserCarEntity?

    @Upsert
    suspend fun upsert(car: UserCarEntity)

    @Query("UPDATE user_cars SET isCurrent = 0")
    suspend fun clearCurrentCar()

    @Query("UPDATE user_cars SET isCurrent = 1 WHERE id = :id")
    suspend fun setCurrentCar(id: String)

    @Query("DELETE FROM user_cars WHERE id = :id")
    suspend fun deleteCar(id: String)
}
