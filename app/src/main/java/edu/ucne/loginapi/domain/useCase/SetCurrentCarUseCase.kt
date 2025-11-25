package edu.ucne.loginapi.domain.useCase

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.repository.UserCarRepository
import javax.inject.Inject

class SetCurrentCarUseCase @Inject constructor(
    private val repository: UserCarRepository
) {
    suspend operator fun invoke(carId: String): Resource<Unit> =
        repository.setCurrentCar(carId)
}
