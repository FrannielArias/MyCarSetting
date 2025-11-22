package edu.ucne.loginapi.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserCarEntity::class,
        MaintenanceTaskEntity::class,
        MaintenanceHistoryEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class MyCarSettingDatabase : RoomDatabase() {
    abstract val userCarDao: UserCarDao
    abstract val maintenanceTaskDao: MaintenanceTaskDao
    abstract val maintenanceHistoryDao: MaintenanceHistoryDao
    abstract val chatMessageDao: ChatMessageDao
}
