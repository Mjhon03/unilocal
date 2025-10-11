package co.edu.eam.unilocal.models

import kotlinx.serialization.Serializable

@Serializable
data class Place(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val description: String = "",
    val address: String = "",
    val phone: String = "",
    val openingTime: String = "",
    val closingTime: String = "",
    val workingDays: List<String> = emptyList(),
    val photoUrls: List<String> = emptyList(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val rating: Double = 0.0,
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isApproved: Boolean = false
)

