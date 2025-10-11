package co.edu.eam.unilocal.models

import kotlinx.serialization.Serializable

@Serializable
data class Place(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val address: String = "",
    val category: String = "",
    val phone: String = "",
    val photoUrls: List<String> = emptyList(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val openingTime: String = "",
    val closingTime: String = "",
    val createdAt: Long = 0L,
    val createdBy: String = "",
    val approved: Boolean = false
)

