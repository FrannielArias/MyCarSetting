package edu.ucne.loginapi.domain.repository

import edu.ucne.loginapi.domain.model.SessionInfo
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun getSession(): Flow<SessionInfo>
    suspend fun saveSession(info: SessionInfo)
    suspend fun clearSession()

}
