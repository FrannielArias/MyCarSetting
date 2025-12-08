package edu.ucne.loginapi.data.syncWorker

import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.repository.CarRepository
import edu.ucne.loginapi.domain.repository.MaintenanceRepository
import edu.ucne.loginapi.domain.repository.MaintenanceTaskRepository
import edu.ucne.loginapi.domain.repository.UserCarRepository
import javax.inject.Inject

class SyncAllUseCase @Inject constructor(
    private val userCarRepository: UserCarRepository,
    private val carRepository: CarRepository,
    private val maintenanceRepository: MaintenanceRepository,
    private val maintenanceTaskRepository: MaintenanceTaskRepository
) {

    suspend operator fun invoke(): Resource<Unit> {
        return try {
            when (val carsPush = carRepository.pushPendingCars()) {
                is Resource.Error -> return Resource.Error(carsPush.message ?: "Error al sincronizar vehículos pendientes")
                else -> Unit
            }

            when (val carsSync = carRepository.syncCars()) {
                is Resource.Error -> return Resource.Error(carsSync.message ?: "Error al sincronizar vehículos")
                else -> Unit
            }

            val currentCar = userCarRepository.getCurrentCar()
            if (currentCar == null) {
                return Resource.Success(Unit)
            }

            when (val tasksPush = maintenanceTaskRepository.postPendingTasks()) {
                is Resource.Error -> return Resource.Error(tasksPush.message ?: "Error al sincronizar tareas pendientes")
                else -> Unit
            }

            when (val maintSync = maintenanceRepository.syncFromRemote(currentCar.id)) {
                is Resource.Error -> return Resource.Error(maintSync.message ?: "Error al sincronizar mantenimiento")
                else -> Unit
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error general de sincronización")
        }
    }

}
