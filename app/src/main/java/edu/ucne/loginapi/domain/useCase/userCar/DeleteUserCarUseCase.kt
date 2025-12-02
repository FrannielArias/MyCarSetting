package edu.ucne.loginapi.domain.useCase.userCar

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.repository.UserCarRepository
import jakarta.inject.Inject

class DeleteUserCarUseCase @Inject constructor(
    private val repository: UserCarRepository
) {
    suspend operator fun invoke(carId: Int): Resource<Unit> =
        repository.deleteCar(carId)
}