package edu.ucne.loginapi.domain.validation

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)
