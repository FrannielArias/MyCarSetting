package edu.ucne.loginapi.domain.useCase.userCar

import edu.ucne.loginapi.domain.model.UserCar
import edu.ucne.loginapi.domain.repository.UserCarRepository
import javax.inject.Inject

class GetCurrentCarUseCase @Inject constructor(
    private val repository: UserCarRepository
) {
    suspend operator fun invoke(): UserCar? = repository.getCurrentCar()
}