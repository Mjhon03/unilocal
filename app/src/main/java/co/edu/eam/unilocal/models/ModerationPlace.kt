package co.edu.eam.unilocal.models

import kotlinx.serialization.Serializable

@Serializable
data class ModerationPlace(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val description: String = "",
    val address: String = "",
    val submittedBy: String = "",
    val submittedByName: String? = null,
    val openingTime: String = "",
    val closingTime: String = "",
    val workingDays: List<String> = emptyList(),
    val phone: String? = null,
    val website: String? = null,
    val photoUrls: List<String> = emptyList(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val createdAt: String = "",
    val status: ModerationStatus = ModerationStatus.PENDING,
    val moderatedBy: String? = null,         
    val moderatedByName: String? = null,     
    val moderatedAt: Long? = null            
)

@Serializable
enum class ModerationStatus {
    PENDING,
    APPROVED,
    REJECTED
}
