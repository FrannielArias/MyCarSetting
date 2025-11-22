package edu.ucne.loginapi.domain.useCase

import edu.ucne.loginapi.domain.model.UserCar
import edu.ucne.loginapi.domain.repository.UserCarRepository
import kotlinx.coroutines.flow.Flow

class ObserveCarsUseCase(
    private val repository: UserCarRepository
) {
    operator fun invoke(): Flow<List<UserCar>> = repository.observeCars()
}
