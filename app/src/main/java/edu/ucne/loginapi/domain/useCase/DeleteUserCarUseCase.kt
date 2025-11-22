package edu.ucne.loginapi.domain.useCase

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.repository.UserCarRepository

class DeleteUserCarUseCase(
    private val repository: UserCarRepository
) {
    suspend operator fun invoke(carId: String): Resource<Unit> =
        repository.deleteCar(carId)
}
