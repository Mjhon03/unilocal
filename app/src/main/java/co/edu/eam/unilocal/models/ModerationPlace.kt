package co.edu.eam.unilocal.models

import kotlinx.serialization.Serializable

@Serializable
data class ModerationPlace(
    val id: String,
    val name: String,
    val description: String,
    val address: String,
    val submittedBy: String,
    val phone: String? = null,
    val website: String? = null,
    val imageUrl: String,
    val createdAt: String,
    val status: ModerationStatus = ModerationStatus.PENDING
)

@Serializable
enum class ModerationStatus {
    PENDING,
    APPROVED,
    REJECTED
}
