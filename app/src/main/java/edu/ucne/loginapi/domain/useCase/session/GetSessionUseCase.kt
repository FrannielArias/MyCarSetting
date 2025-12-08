package edu.ucne.loginapi.domain.useCase.session

import edu.ucne.loginapi.domain.model.SessionInfo
import edu.ucne.loginapi.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSessionUseCase @Inject constructor(
    private val repository: SessionRepository
) {
    operator fun invoke(): Flow<SessionInfo> = repository.getSession()

}