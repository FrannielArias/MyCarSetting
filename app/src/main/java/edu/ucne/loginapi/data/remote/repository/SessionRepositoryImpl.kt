package edu.ucne.loginapi.data.remote.repository

import edu.ucne.loginapi.data.SessionDataStore
import edu.ucne.loginapi.domain.model.SessionInfo
import edu.ucne.loginapi.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val sessionDataStore: SessionDataStore
) : SessionRepository {

    override fun getSession(): Flow<SessionInfo> = sessionDataStore.getSession()

    override suspend fun saveSession(info: SessionInfo) {
        sessionDataStore.saveSession(info)
    }

    override suspend fun clearSession() {
        sessionDataStore.clearSession()
    }
}
