package edu.ucne.loginapi.data.syncWorker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import edu.ucne.loginapi.data.remote.Resource
import edu.ucne.loginapi.domain.repository.CarRepository
import edu.ucne.loginapi.domain.repository.MaintenanceRepository
import edu.ucne.loginapi.domain.repository.MaintenanceTaskRepository
import edu.ucne.loginapi.domain.repository.UserCarRepository

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val userCarRepository: UserCarRepository,
    private val carRepository: CarRepository,
    private val maintenanceRepository: MaintenanceRepository,
    private val maintenanceTaskRepository: MaintenanceTaskRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val currentCar = userCarRepository.getCurrentCar()

            // 1) Empujar vehículos pendientes
            when (carRepository.pushPendingCars()) {
                is Resource.Error -> return Result.retry()
                else -> Unit
            }

            // 2) Sincronizar catálogo de vehículos
            when (carRepository.syncCars()) {
                is Resource.Error -> return Result.retry()
                else -> Unit
            }

            // 3) Si no hay vehículo actual, termina ok
            if (currentCar == null) {
                return Result.success()
            }

            // 4) Empujar tareas de mantenimiento pendientes
            when (maintenanceTaskRepository.postPendingTasks()) {
                is Resource.Error -> return Result.retry()
                else -> Unit
            }

            // 5) Traer tareas / historial del servidor
            when (maintenanceRepository.syncFromRemote(currentCar.id)) {
                is Resource.Error -> Result.retry()
                else -> Result.success()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}