package edu.ucne.loginapi.data.remote.dto

data class WarningLightDto(
    val id: Int = 0,
    val code: String?,
    val name: String,
    val description: String,
    val action: String,
    val severity: String?
)

data class GuideArticleDto(
    val id: Int = 0,
    val title: String,
    val summary: String,
    val content: String,
    val category: String?
)
