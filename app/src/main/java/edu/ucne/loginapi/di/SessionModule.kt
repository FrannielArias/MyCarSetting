package edu.ucne.loginapi.di

import edu.ucne.loginapi.data.remote.repository.SessionRepositoryImpl
import edu.ucne.loginapi.domain.repository.SessionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface SessionModule {

    @Binds
    @Singleton
    fun bindSessionRepository(
        impl: SessionRepositoryImpl
    ): SessionRepository
}
