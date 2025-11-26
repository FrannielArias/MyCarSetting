package edu.ucne.loginapi.domain.validation

import javax.inject.Inject

class ValidatePasswordUseCase @Inject constructor() {
    operator fun invoke(password: String): ValidationResult {
        if (password.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "La contraseña es obligatoria"
            )
        }
        if (password.length < 4) {
            return ValidationResult(
                successful = false,
                errorMessage = "La contraseña debe tener al menos 4 caracteres"
            )
        }
        return ValidationResult(successful = true)
    }
}
