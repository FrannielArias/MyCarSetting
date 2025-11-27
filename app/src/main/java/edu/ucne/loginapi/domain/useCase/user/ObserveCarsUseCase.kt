package edu.ucne.loginapi.domain.useCase.user

import edu.ucne.loginapi.domain.model.UserCar
import edu.ucne.loginapi.domain.repository.UserCarRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveCarsUseCase @Inject constructor(
    private val repository: UserCarRepository
) {
    operator fun invoke(): Flow<List<UserCar>> = repository.observeCars()
}
