package edu.ucne.loginapi.data.mapper

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
import edu.ucne.loginapi.domain.model.WarningLight

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

fun ChatMessageDto.toDomain(): ChatMessage =
    ChatMessage(
        id = id,
        conversationId = conversationId,
        role = role.toChatRole(),
        content = content,
        timestampMillis = timestampMillis,
        isPendingCreate = false
    )

fun ChatMessage.toDto(): ChatMessageDto =
    ChatMessageDto(
        id = id,
        conversationId = conversationId,
        role = role.toDto(),
        content = content,
        timestampMillis = timestampMillis
    )

fun ChatResponseDto.toDomain(conversationIdOverride: String? = null): ChatMessage =
    ChatMessage(
        id = messageId,
        conversationId = conversationIdOverride ?: conversationId,
        role = ChatRole.ASSISTANT,
        content = reply,
        timestampMillis = timestampMillis,
        isPendingCreate = false
    )

fun ChatMessage.toChatRequest(vehicleId: String?): ChatRequestDto =
    ChatRequestDto(
        conversationId = conversationId,
        userMessage = content,
        vehicleId = vehicleId
    )
