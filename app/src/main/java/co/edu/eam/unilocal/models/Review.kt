package co.edu.eam.unilocal.models

import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val id: String = "",
    val placeId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userInitials: String = "",
    val rating: Int = 0, // 1-5 estrellas
    val comment: String = "",
    val createdAt: Long = System.currentTimeMillis()
)