package edu.ucne.loginapi.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import edu.ucne.loginapi.domain.repository.CarRepository
import edu.ucne.loginapi.domain.repository.MaintenanceRepository
import edu.ucne.loginapi.domain.repository.ManualRepository
import edu.ucne.loginapi.domain.repository.ChatRepository
import edu.ucne.loginapi.data.repository.CarRepositoryImpl
import edu.ucne.loginapi.data.repository.MaintenanceRepositoryImpl
import edu.ucne.loginapi.data.repository.ManualRepositoryImpl
import edu.ucne.loginapi.data.repository.ChatRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCarRepository(
        impl: CarRepositoryImpl
    ): CarRepository

    @Binds
    @Singleton
    abstract fun bindMaintenanceRepository(
        impl: MaintenanceRepositoryImpl
    ): MaintenanceRepository

    @Binds
    @Singleton
    abstract fun bindManualRepository(
        impl: ManualRepositoryImpl
    ): ManualRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        impl: ChatRepositoryImpl
    ): ChatRepository
}
