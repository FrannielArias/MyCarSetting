package edu.ucne.loginapi.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.ucne.loginapi.data.ChatMessageDao
import edu.ucne.loginapi.data.MaintenanceHistoryDao
import edu.ucne.loginapi.data.MaintenanceTaskDao
import edu.ucne.loginapi.data.MyCarSettingDatabase
import edu.ucne.loginapi.data.local.dao.UserCarDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): MyCarSettingDatabase =
        Room.databaseBuilder(
            context,
            MyCarSettingDatabase::class.java,
            "my_car_setting.db"
        ).build()

    @Provides
    fun provideUserCarDao(
        db: MyCarSettingDatabase
    ): UserCarDao = db.userCarDao

    @Provides
    fun provideMaintenanceTaskDao(
        db: MyCarSettingDatabase
    ): MaintenanceTaskDao = db.maintenanceTaskDao

    @Provides
    fun provideMaintenanceHistoryDao(
        db: MyCarSettingDatabase
    ): MaintenanceHistoryDao = db.maintenanceHistoryDao

    @Provides
    fun provideChatMessageDao(
        db: MyCarSettingDatabase
    ): ChatMessageDao = db.chatMessageDao
}
