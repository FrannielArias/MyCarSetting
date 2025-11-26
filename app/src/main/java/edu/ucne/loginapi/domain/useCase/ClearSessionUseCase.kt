package edu.ucne.loginapi.domain.useCase

import edu.ucne.loginapi.domain.repository.SessionRepository
import javax.inject.Inject

class ClearSessionUseCase @Inject constructor(
    private val repository: SessionRepository
) {
    suspend operator fun invoke() {
        repository.clearSession()
    }
}
