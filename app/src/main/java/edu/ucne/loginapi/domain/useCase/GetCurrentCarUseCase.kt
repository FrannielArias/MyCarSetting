package edu.ucne.loginapi.domain.useCase

import edu.ucne.loginapi.domain.model.UserCar
import edu.ucne.loginapi.domain.repository.UserCarRepository

class GetCurrentCarUseCase(
    private val repository: UserCarRepository
) {
    suspend operator fun invoke(): UserCar? = repository.getCurrentCar()
}
