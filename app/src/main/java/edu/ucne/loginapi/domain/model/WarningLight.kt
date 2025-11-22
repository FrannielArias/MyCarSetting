package edu.ucne.loginapi.domain.model

import java.util.UUID

data class WarningLight(
    val id: String = UUID.randomUUID().toString(),
    val code: String?,
    val name: String,
    val severity: WarningSeverity,
    val description: String,
    val recommendedAction: String
)

enum class WarningSeverity {
    NORMAL,
    CAUTION,
    DANGER
}
