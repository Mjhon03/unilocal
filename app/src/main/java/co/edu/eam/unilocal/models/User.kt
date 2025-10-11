package co.edu.eam.unilocal.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val phone: String = "",
    val city: String = "",
    val profileImageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val role: UserRole = UserRole.USER
)

@Serializable
enum class UserRole {
    USER,
    MODERATOR,
    ADMIN
}

