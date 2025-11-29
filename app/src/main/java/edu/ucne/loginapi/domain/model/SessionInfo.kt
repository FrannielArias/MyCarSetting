package edu.ucne.loginapi.domain.model

data class SessionInfo(
    val isLoggedIn: Boolean,
    val userId: Int? = null,
    val userName: String? = null
)
