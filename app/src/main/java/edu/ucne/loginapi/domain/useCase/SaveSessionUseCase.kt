package edu.ucne.loginapi.domain.useCase

import edu.ucne.loginapi.domain.model.SessionInfo
import edu.ucne.loginapi.domain.repository.SessionRepository
import javax.inject.Inject

class SaveSessionUseCase @Inject constructor(
    private val repository: SessionRepository
) {
    suspend operator fun invoke(info: SessionInfo) {
        repository.saveSession(info)
    }
}
