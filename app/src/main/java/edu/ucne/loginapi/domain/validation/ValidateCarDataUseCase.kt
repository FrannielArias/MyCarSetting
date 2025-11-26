package edu.ucne.loginapi.domain.validation

import javax.inject.Inject

class ValidateCarDataUseCase @Inject constructor() {
    operator fun invoke(
        brand: String,
        model: String,
        yearText: String
    ): ValidationResult {
        if (brand.isBlank() || model.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Marca y modelo son requeridos"
            )
        }

        val year = yearText.toIntOrNull()
        if (year == null || year < 1980 || year > 2100) {
            return ValidationResult(
                successful = false,
                errorMessage = "Año inválido"
            )
        }

        return ValidationResult(successful = true)
    }
}
