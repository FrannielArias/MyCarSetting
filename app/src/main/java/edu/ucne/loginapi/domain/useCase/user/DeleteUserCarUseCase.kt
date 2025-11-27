package edu.ucne.loginapi.domain.useCase.user

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.repository.UserCarRepository
import jakarta.inject.Inject

class DeleteUserCarUseCase @Inject constructor(
    private val repository: UserCarRepository
) {
    suspend operator fun invoke(carId: String): Resource<Unit> =
        repository.deleteCar(carId)
}
