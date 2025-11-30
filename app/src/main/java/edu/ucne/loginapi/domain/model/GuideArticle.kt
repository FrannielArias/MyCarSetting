package edu.ucne.loginapi.domain.model

data class GuideArticle(
    val id: Int,
    val title: String,
    val summary: String,
    val content: String,
    val category: String?
)
