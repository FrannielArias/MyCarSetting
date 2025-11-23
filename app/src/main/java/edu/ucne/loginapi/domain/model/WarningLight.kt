package edu.ucne.loginapi.domain.model

data class WarningLight(
    val id: String,
    val code: String?,
    val name: String,
    val description: String,
    val action: String,
    val severity: String?
)
