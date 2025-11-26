package edu.ucne.loginapi.domain.validation

import javax.inject.Inject

class ValidateUserNameUseCase @Inject constructor() {
    operator fun invoke(userName: String): ValidationResult {
        if (userName.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "El nombre de usuario es obligatorio"
            )
        }
        if (userName.length < 3) {
            return ValidationResult(
                successful = false,
                errorMessage = "El nombre de usuario debe tener al menos 3 caracteres"
            )
        }
        return ValidationResult(successful = true)
    }
}
