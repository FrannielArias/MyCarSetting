package edu.ucne.loginapi.domain.useCase.user

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.model.UserCar
import edu.ucne.loginapi.domain.repository.UserCarRepository
import javax.inject.Inject

class AddUserCarUseCase @Inject constructor(
    private val repository: UserCarRepository
) {
    suspend operator fun invoke(car: UserCar): Resource<UserCar> =
        repository.upsertCar(car)
}