package edu.ucne.loginapi.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.ucne.loginapi.data.MyCarSettingDatabase
import edu.ucne.loginapi.data.dao.ChatMessageDao
import edu.ucne.loginapi.data.dao.MaintenanceHistoryDao
import edu.ucne.loginapi.data.dao.MaintenanceTaskDao
import edu.ucne.loginapi.data.dao.UserCarDao
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
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideUserCarDao(
        db: MyCarSettingDatabase
    ): UserCarDao = db.userCarDao

    @Provides
    @Singleton
    fun provideMaintenanceTaskDao(
        db: MyCarSettingDatabase
    ): MaintenanceTaskDao = db.maintenanceTaskDao

    @Provides
    @Singleton
    fun provideMaintenanceHistoryDao(
        db: MyCarSettingDatabase
    ): MaintenanceHistoryDao = db.maintenanceHistoryDao

    @Provides
    @Singleton
    fun provideChatMessageDao(
        db: MyCarSettingDatabase
    ): ChatMessageDao = db.chatMessageDao
}
