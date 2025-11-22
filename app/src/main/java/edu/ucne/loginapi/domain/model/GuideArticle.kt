package edu.ucne.loginapi.domain.model

import java.util.UUID

data class GuideArticle(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val shortDescription: String,
    val category: GuideCategory,
    val steps: List<GuideStep>
)

data class GuideStep(
    val order: Int,
    val title: String,
    val description: String
)

enum class GuideCategory {
    BASIC_CARE,
    EMERGENCY,
    DIY_REPAIR,
    ELECTRICAL,
    TIRES,
    FLUIDS,
    OTHER
}
