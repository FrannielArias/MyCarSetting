package edu.ucne.loginapi.presentation.Register

data class RegisterUiState(
    val userName: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val navigateBack: Boolean = false
)
