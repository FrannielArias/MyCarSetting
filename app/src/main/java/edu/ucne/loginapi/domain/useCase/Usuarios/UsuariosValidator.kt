package edu.ucne.loginapi.domain.useCase.Usuarios

data class UsuariosValidator(
    val isValid: Boolean,
    val error: String? = null
)

fun validateUserName(value: String): UsuariosValidator{
    if(value.isBlank())
        return UsuariosValidator(false,"El UserName no puede estar vacia.")

    if(value.length < 4)
        return UsuariosValidator(false , "El UserName debe tener mas de 3 letras")

    return UsuariosValidator(true)
}