package edu.ucne.loginapi.data

import androidx.room.Database
import androidx.room.RoomDatabase
import edu.ucne.loginapi.data.Entity.ChatMessageEntity
import edu.ucne.loginapi.data.Entity.MaintenanceHistoryEntity
import edu.ucne.loginapi.data.Entity.MaintenanceTaskEntity
import edu.ucne.loginapi.data.Entity.UserCarEntity
import edu.ucne.loginapi.data.dao.ChatMessageDao
import edu.ucne.loginapi.data.dao.MaintenanceHistoryDao
import edu.ucne.loginapi.data.dao.MaintenanceTaskDao
import edu.ucne.loginapi.data.dao.UserCarDao

@Database(
    entities = [
        UserCarEntity::class,
        MaintenanceTaskEntity::class,
        MaintenanceHistoryEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MyCarSettingDatabase : RoomDatabase() {
    abstract val userCarDao: UserCarDao
    abstract val maintenanceTaskDao: MaintenanceTaskDao
    abstract val maintenanceHistoryDao: MaintenanceHistoryDao
    abstract val chatMessageDao: ChatMessageDao
}
