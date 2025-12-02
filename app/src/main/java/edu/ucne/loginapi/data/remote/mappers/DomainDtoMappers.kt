package edu.ucne.loginapi.data.remote.mappers

import edu.ucne.loginapi.data.entity.ChatMessageEntity
import edu.ucne.loginapi.data.entity.MaintenanceHistoryEntity
import edu.ucne.loginapi.domain.model.MaintenanceSeverity
import edu.ucne.loginapi.data.entity.MaintenanceTaskEntity
import edu.ucne.loginapi.data.entity.UserCarEntity
import edu.ucne.loginapi.data.remote.dto.ChatMessageDto
import edu.ucne.loginapi.data.remote.dto.ChatRequestDto
import edu.ucne.loginapi.data.remote.dto.ChatResponseDto
import edu.ucne.loginapi.data.remote.dto.CreateMaintenanceHistoryRequest
import edu.ucne.loginapi.data.remote.dto.CreateMaintenanceTaskRequest
import edu.ucne.loginapi.data.remote.dto.CreateUserCarRequest
import edu.ucne.loginapi.data.remote.dto.GuideArticleDto
import edu.ucne.loginapi.data.remote.dto.MaintenanceHistoryDto
import edu.ucne.loginapi.data.remote.dto.MaintenanceTaskDto
import edu.ucne.loginapi.data.remote.dto.UpdateMaintenanceTaskRequest
import edu.ucne.loginapi.data.remote.dto.UpdateUserCarRequest
import edu.ucne.loginapi.data.remote.dto.UserCarDto
import edu.ucne.loginapi.data.remote.dto.UsuariosDto
import edu.ucne.loginapi.data.remote.dto.VehicleBrandDto
import edu.ucne.loginapi.data.remote.dto.VehicleModelDto
import edu.ucne.loginapi.data.remote.dto.VehicleYearRangeDto
import edu.ucne.loginapi.data.remote.dto.WarningLightDto
import edu.ucne.loginapi.domain.model.ChatMessage
import edu.ucne.loginapi.domain.model.ChatRole
import edu.ucne.loginapi.domain.model.FuelType
import edu.ucne.loginapi.domain.model.GuideArticle
import edu.ucne.loginapi.domain.model.MaintenanceHistory
import edu.ucne.loginapi.domain.model.MaintenanceStatus
import edu.ucne.loginapi.domain.model.MaintenanceTask
import edu.ucne.loginapi.domain.model.MaintenanceType
import edu.ucne.loginapi.domain.model.UsageType
import edu.ucne.loginapi.domain.model.UserCar
import edu.ucne.loginapi.domain.model.Usuarios
import edu.ucne.loginapi.domain.model.VehicleBrand
import edu.ucne.loginapi.domain.model.VehicleModel
import edu.ucne.loginapi.domain.model.VehicleYearRange
import edu.ucne.loginapi.domain.model.WarningLight
import java.util.UUID

fun FuelType.toDto(): String = name

fun String.toFuelType(): FuelType =
    runCatching { FuelType.valueOf(this) }.getOrElse { FuelType.GASOLINE }

fun UsageType.toDto(): String = name

fun String.toUsageType(): UsageType =
    runCatching { UsageType.valueOf(this) }.getOrElse { UsageType.PERSONAL }

fun MaintenanceType.toDto(): String = name

fun String.toMaintenanceType(): MaintenanceType =
    runCatching { MaintenanceType.valueOf(this) }.getOrElse { MaintenanceType.OTHER }

fun MaintenanceStatus.toDto(): String = name

fun String.toMaintenanceStatus(): MaintenanceStatus =
    runCatching { MaintenanceStatus.valueOf(this) }.getOrElse { MaintenanceStatus.UPCOMING }

fun ChatRole.toDto(): String = name

fun String.toChatRole(): ChatRole =
    runCatching { ChatRole.valueOf(this) }.getOrElse { ChatRole.ASSISTANT }

fun UserCarDto.toDomain(): UserCar =
    UserCar(
        id = id,
        brand = brand,
        model = model,
        year = year,
        plate = plate,
        fuelType = fuelType.toFuelType(),
        usageType = usageType.toUsageType(),
        isCurrent = isCurrent
    )

fun UserCar.toDto(): UserCarDto =
    UserCarDto(
        id = id,
        brand = brand,
        model = model,
        year = year,
        plate = plate,
        fuelType = fuelType.toDto(),
        usageType = usageType.toDto(),
        isCurrent = isCurrent
    )

fun UserCar.toCreateRequest(): CreateUserCarRequest =
    CreateUserCarRequest(
        brand = brand,
        model = model,
        year = year,
        plate = plate,
        fuelType = fuelType.toDto(),
        usageType = usageType.toDto()
    )

fun UserCar.toUpdateRequest(): UpdateUserCarRequest =
    UpdateUserCarRequest(
        brand = brand,
        model = model,
        year = year,
        plate = plate,
        fuelType = fuelType.toDto(),
        usageType = usageType.toDto(),
        isCurrent = isCurrent
    )

fun MaintenanceTaskDto.toDomain(): MaintenanceTask =
    MaintenanceTask(
        id = id,
        carId = carId,
        type = type.toMaintenanceType(),
        title = title,
        description = description,
        dueDateMillis = dueDateMillis,
        dueMileageKm = dueMileageKm,
        status = status.toMaintenanceStatus(),
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis
    )

fun MaintenanceTask.toDto(): MaintenanceTaskDto =
    MaintenanceTaskDto(
        id = id,
        carId = carId,
        type = type.toDto(),
        title = title,
        description = description,
        dueDateMillis = dueDateMillis,
        dueMileageKm = dueMileageKm,
        status = status.toDto(),
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis
    )

fun MaintenanceTask.toCreateRequest(): CreateMaintenanceTaskRequest =
    CreateMaintenanceTaskRequest(
        carId = carId,
        type = type.toDto(),
        title = title,
        description = description,
        dueDateMillis = dueDateMillis,
        dueMileageKm = dueMileageKm
    )

fun MaintenanceTask.toUpdateRequest(): UpdateMaintenanceTaskRequest =
    UpdateMaintenanceTaskRequest(
        type = type.toDto(),
        title = title,
        description = description,
        dueDateMillis = dueDateMillis,
        dueMileageKm = dueMileageKm,
        status = status.toDto()
    )

fun MaintenanceHistoryDto.toDomain(): MaintenanceHistory =
    MaintenanceHistory(
        id = id,
        carId = carId,
        taskType = taskType.toMaintenanceType(),
        serviceDateMillis = serviceDateMillis,
        mileageKm = mileageKm,
        workshopName = workshopName,
        cost = cost,
        notes = notes
    )

fun MaintenanceHistory.toDto(): MaintenanceHistoryDto =
    MaintenanceHistoryDto(
        id = id,
        carId = carId,
        taskType = taskType.toDto(),
        serviceDateMillis = serviceDateMillis,
        mileageKm = mileageKm,
        workshopName = workshopName,
        cost = cost,
        notes = notes
    )

fun MaintenanceHistory.toCreateRequest(): CreateMaintenanceHistoryRequest =
    CreateMaintenanceHistoryRequest(
        carId = carId,
        taskType = taskType.toDto(),
        serviceDateMillis = serviceDateMillis,
        mileageKm = mileageKm,
        workshopName = workshopName,
        cost = cost,
        notes = notes
    )

fun WarningLightDto.toDomain(): WarningLight =
    WarningLight(
        id = id,
        code = code,
        name = name,
        description = description,
        action = action,
        severity = severity
    )

fun WarningLight.toDto(): WarningLightDto =
    WarningLightDto(
        id = id,
        code = code,
        name = name,
        description = description,
        action = action,
        severity = severity
    )

fun GuideArticleDto.toDomain(): GuideArticle =
    GuideArticle(
        id = id,
        title = title,
        summary = summary,
        content = content,
        category = category
    )

fun GuideArticle.toDto(): GuideArticleDto =
    GuideArticleDto(
        id = id,
        title = title,
        summary = summary,
        content = content,
        category = category
    )

fun List<ChatMessage>.toChatRequestDto(): ChatRequestDto {
    val conversationId = lastOrNull()?.conversationId ?: ""
    return ChatRequestDto(
        conversationId = conversationId,
        messages = map { it.toDto() }
    )
}

fun ChatResponseDto.toResponseDomain(conversationId: String): ChatMessage =
    ChatMessage(
        id = UUID.randomUUID().toString(),
        conversationId = conversationId,
        role = ChatRole.ASSISTANT,
        content = reply,
        timestampMillis = System.currentTimeMillis(),
        isPendingSync = false
    )

fun UsuariosDto.toDomain(): Usuarios =
    Usuarios(
        usuarioId = usuarioId,
        userName = userName,
        password = password
    )

fun Usuarios.toDto(): UsuariosDto =
    UsuariosDto(
        usuarioId = usuarioId,
        userName = userName,
        password = password
    )

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
        severity = runCatching { MaintenanceSeverity.valueOf(severity) }
            .getOrElse { MaintenanceSeverity.MEDIUM },
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
        severity = severity.name,
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
        workshopName = workshopName,
        cost = cost,
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
        workshopName = workshopName,
        cost = cost,
        notes = notes
    )
}

fun ChatMessageEntity.toDomain(): ChatMessage {
    return ChatMessage(
        id = id.toString(),
        conversationId = conversationId,
        role = role.toChatRole(),
        content = content,
        timestampMillis = timestamp,
        isPendingSync = false
    )
}

fun ChatMessage.toEntity(): ChatMessageEntity {
    return ChatMessageEntity(
        id = 0L,
        conversationId = conversationId,
        role = role.name,
        content = content,
        timestamp = timestampMillis
    )
}

fun ChatMessage.toDto(): ChatMessageDto {
    return ChatMessageDto(
        role = role.name.lowercase(),
        content = content
    )
}
fun VehicleBrandDto.toDomain(): VehicleBrand =
    VehicleBrand(
        id = id,
        name = name
    )

fun VehicleModelDto.toDomain(): VehicleModel =
    VehicleModel(
        id = id,
        brandId = brandId,
        name = name
    )

fun VehicleYearRangeDto.toDomain(): VehicleYearRange =
    VehicleYearRange(
        id = id,
        modelId = modelId,
        fromYear = fromYear,
        toYear = toYear
    )

