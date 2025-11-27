package edu.ucne.loginapi.data.remote.mappers

import edu.ucne.loginapi.data.remote.dto.UsuariosDto
import edu.ucne.loginapi.domain.model.Usuarios

fun UsuariosDto.toDomain(): Usuarios =
    Usuarios(
        usuarioId = usuarioId,
        userName = userName,
        password = password
    )

fun Usuarios.toDto(): UsuariosDto =
    UsuariosDto(
        usuarioId = usuarioId,
        userName = userName,
        password = password
    )
