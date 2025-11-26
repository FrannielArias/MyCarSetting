package edu.ucne.loginapi.data.remote.mappers

import edu.ucne.loginapi.data.entity.ChatMessageEntity
import edu.ucne.loginapi.data.entity.MaintenanceHistoryEntity
import edu.ucne.loginapi.data.entity.MaintenanceTaskEntity
import edu.ucne.loginapi.data.entity.UserCarEntity
import edu.ucne.loginapi.domain.model.ChatMessage
import edu.ucne.loginapi.domain.model.ChatRole
import edu.ucne.loginapi.domain.model.FuelType
import edu.ucne.loginapi.domain.model.MaintenanceHistory
import edu.ucne.loginapi.domain.model.MaintenanceStatus
import edu.ucne.loginapi.domain.model.MaintenanceTask
import edu.ucne.loginapi.domain.model.MaintenanceType
import edu.ucne.loginapi.domain.model.UserCar
import edu.ucne.loginapi.domain.model.UsageType

fun UserCarEntity.toDomain(): UserCar {
    return UserCar(
        id = id,
        brand = brand,
        model = model,
        year = year,
        plate = plate,
        fuelType = FuelType.valueOf(fuelType),
        usageType = UsageType.valueOf(usageType),
        isCurrent = isCurrent,
        remoteId = remoteId
    )
}

fun UserCar.toEntity(): UserCarEntity {
    return UserCarEntity(
        id = id,
        brand = brand,
        model = model,
        year = year,
        plate = plate,
        fuelType = fuelType.name,
        usageType = usageType.name,
        isCurrent = isCurrent,
        remoteId = remoteId
    )
}

fun MaintenanceTaskEntity.toDomain(): MaintenanceTask {
    return MaintenanceTask(
        id = id,
        remoteId = remoteId,
        carId = carId,
        type = MaintenanceType.valueOf(type),
        title = title,
        description = description,
        dueDateMillis = dueDateMillis,
        dueMileageKm = dueMileageKm,
        status = MaintenanceStatus.valueOf(status),
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis,
        isPendingCreate = isPendingCreate,
        isPendingUpdate = isPendingUpdate,
        isPendingDelete = isPendingDelete
    )
}

fun MaintenanceTask.toEntity(): MaintenanceTaskEntity {
    return MaintenanceTaskEntity(
        id = id,
        remoteId = remoteId,
        carId = carId,
        type = type.name,
        title = title,
        description = description,
        dueDateMillis = dueDateMillis,
        dueMileageKm = dueMileageKm,
        status = status.name,
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis,
        isPendingCreate = isPendingCreate,
        isPendingUpdate = isPendingUpdate,
        isPendingDelete = isPendingDelete
    )
}

fun MaintenanceHistoryEntity.toDomain(): MaintenanceHistory {
    return MaintenanceHistory(
        id = id,
        carId = carId,
        taskType = MaintenanceType.valueOf(taskType),
        serviceDateMillis = serviceDateMillis,
        mileageKm = mileageKm,
        cost = cost,
        workshopName = workshopName,
        notes = notes
    )
}

fun MaintenanceHistory.toEntity(): MaintenanceHistoryEntity {
    return MaintenanceHistoryEntity(
        id = id,
        carId = carId,
        taskType = taskType.name,
        serviceDateMillis = serviceDateMillis,
        mileageKm = mileageKm,
        cost = cost,
        workshopName = workshopName,
        notes = notes
    )
}

fun ChatMessageEntity.toDomain(): ChatMessage {
    return ChatMessage(
        id = id,
        conversationId = conversationId,
        role = ChatRole.valueOf(role),
        content = content,
        timestampMillis = timestampMillis,
        isPendingCreate = isPendingCreate
    )
}

fun ChatMessage.toEntity(): ChatMessageEntity {
    return ChatMessageEntity(
        id = id,
        conversationId = conversationId,
        role = role.name,
        content = content,
        timestampMillis = timestampMillis,
        isPendingCreate = isPendingCreate
    )
}
