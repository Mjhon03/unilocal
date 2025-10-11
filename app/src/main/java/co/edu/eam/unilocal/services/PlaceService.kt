package co.edu.eam.unilocal.services

import android.util.Log
import co.edu.eam.unilocal.models.ModerationPlace
import co.edu.eam.unilocal.models.Place
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import co.edu.eam.unilocal.models.ModerationStatus

class PlaceService {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val placesCollection = firestore.collection("places")

    /**
     * Crea una entrada de moderación usando la colección `places`.
     * Se escribe un documento en `places` con isApproved = false para que los admins lo revisen.
     * Devuelve Result.success(documentId) si se creó correctamente.
     */
    suspend fun createModerationPlace(place: ModerationPlace): Result<String> {
        return try {
            Log.d("PlaceService", "Creando lugar para moderación (places): ${place.name}")

            // Generar id y mapear ModerationPlace -> Place
            val newDocRef = placesCollection.document()

            val createdAtLong = place.createdAt.toLongOrNull() ?: System.currentTimeMillis()
            val photos = if (place.imageUrl.isNotBlank()) listOf(place.imageUrl) else emptyList()

            val placeToStore = Place(
                id = newDocRef.id,
                name = place.name,
                category = "Sin categoría",
                description = place.description,
                address = place.address,
                phone = place.phone ?: "",
                openingTime = place.openingTime,
                closingTime = place.closingTime,
                workingDays = place.workingDays,
                photoUrls = photos,
                createdBy = place.submittedBy,
                createdAt = createdAtLong,
                isApproved = false
            )

            newDocRef.set(placeToStore).await()
            // Ensure both possible flag names exist in the document for compatibility
            newDocRef.update(mapOf(
                "isApproved" to false,
                "approved" to false,
                "moderationStatus" to ModerationStatus.PENDING.name
            )).await()

            Log.d("PlaceService", "Lugar creado en places para moderación: ${newDocRef.id}")
            Result.success(newDocRef.id)
        } catch (e: Exception) {
            Log.e("PlaceService", "Error al crear lugar en places: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Obtiene los lugares pendientes desde la colección `places` (isApproved/approved == false).
     * Devuelve la lista mapeada a ModerationPlace para ser mostrada en el panel de moderación.
     */
    suspend fun getPendingModerationPlaces(): Result<List<ModerationPlace>> {
        return try {
            val snapshot = placesCollection.get().await()

            val places = snapshot.documents.mapNotNull { doc ->
                val approvedFlag =
                    (doc.getBoolean("approved") ?: doc.getBoolean("isApproved")) ?: false
                val moderationStatusStr = doc.getString("moderationStatus") ?: ModerationStatus.PENDING.name
                // Exclude already approved and explicitly rejected entries
                if (approvedFlag) return@mapNotNull null
                if (moderationStatusStr == ModerationStatus.REJECTED.name) return@mapNotNull null

                val photoList =
                    (doc.get("photoUrls") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                val imageUrl = photoList.firstOrNull() ?: ""

                val createdAtVal =
                    (doc.getLong("createdAt") ?: (doc.get("createdAt") as? Number)?.toLong())
                        ?: System.currentTimeMillis()
                ModerationPlace(
                    id = doc.id,
                    name = doc.getString("name") ?: "",
                    description = doc.getString("description") ?: "",
                    address = doc.getString("address") ?: "",
                    submittedBy = doc.getString("createdBy") ?: "",
                    phone = doc.getString("phone"),
                    website = doc.getString("website"),
                    imageUrl = imageUrl,
                    createdAt = createdAtVal.toString(),
                    status = ModerationStatus.PENDING
                )
            }

            Result.success(places)
        } catch (e: Exception) {
            Log.e("PlaceService", "Error al obtener moderaciones desde places: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Rechaza una moderación eliminando el documento de `places`.
     */
    suspend fun rejectModerationPlace(moderationId: String): Result<Unit> {
        return try {
            // Mark as rejected and clear approved flags so it moves to history as not approved
            placesCollection.document(moderationId).update(mapOf(
                "moderationStatus" to ModerationStatus.REJECTED.name,
                "isApproved" to false,
                "approved" to false
            )).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("PlaceService", "Error al eliminar lugar pendiente: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Aprueba una moderación actualizando el campo isApproved = true en el documento de `places`.
     */
    suspend fun approveModerationPlace(moderationId: String): Result<String> {
        return try {
            val docRef = placesCollection.document(moderationId)
            val snapshot = docRef.get().await()
            if (!snapshot.exists()) return Result.failure(Exception("Place doc not found"))

            // Mark approved (both flag names) and set moderationStatus
            docRef.update(
                mapOf(
                    "isApproved" to true,
                    "approved" to true,
                    "moderationStatus" to ModerationStatus.APPROVED.name
                )
            ).await()

            Result.success(moderationId)
        } catch (e: Exception) {
            Log.e("PlaceService", "Error al aprobar lugar pendiente: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Obtiene el historial de moderación: tanto aprobados como rechazados.
     */
    suspend fun getModerationHistory(): Result<List<ModerationPlace>> {
        return try {
            val snapshot = placesCollection.get().await()

            val places = snapshot.documents.mapNotNull { doc ->
                val approvedFlag =
                    (doc.getBoolean("approved") ?: doc.getBoolean("isApproved")) ?: false
                val moderationStatusStr =
                    doc.getString("moderationStatus") ?: ModerationStatus.PENDING.name
                // Keep only approved or rejected
                if (moderationStatusStr == ModerationStatus.PENDING.name) return@mapNotNull null

                val photoList =
                    (doc.get("photoUrls") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                val imageUrl = photoList.firstOrNull() ?: ""

                val createdAtVal =
                    (doc.getLong("createdAt") ?: (doc.get("createdAt") as? Number)?.toLong())
                        ?: System.currentTimeMillis()

                ModerationPlace(
                    id = doc.id,
                    name = doc.getString("name") ?: "",
                    description = doc.getString("description") ?: "",
                    address = doc.getString("address") ?: "",
                    submittedBy = doc.getString("createdBy") ?: "",
                    submittedByName = null,
                    openingTime = doc.getString("openingTime") ?: "",
                    closingTime = doc.getString("closingTime") ?: "",
                    workingDays = (doc.get("workingDays") as? List<*>)?.mapNotNull { it as? String }
                        ?: emptyList(),
                    phone = doc.getString("phone"),
                    website = doc.getString("website"),
                    imageUrl = imageUrl,
                    createdAt = createdAtVal.toString(),
                    status = if (moderationStatusStr == ModerationStatus.APPROVED.name) ModerationStatus.APPROVED else ModerationStatus.REJECTED
                )
            }

            Result.success(places)
        } catch (e: Exception) {
            Log.e("PlaceService", "Error al obtener historial de moderación: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Obtiene todos los lugares aprobados desde la colección `places`.
     */
    suspend fun getAllApprovedPlaces(): Result<List<Place>> {
        return try {
            Log.d("PlaceService", "Obteniendo todos los lugares aprobados")
            // Some documents may use the field name "approved" while others use "isApproved".
            // We'll fetch documents and filter for either flag being true.
            val snapshot = placesCollection.get().await()

            val places = snapshot.documents.mapNotNull { doc ->
                val approvedFlag =
                    (doc.getBoolean("approved") ?: doc.getBoolean("isApproved")) ?: false
                if (!approvedFlag) return@mapNotNull null

                // Try to parse into Place; override the isApproved field with the detected flag
                val parsed = doc.toObject(Place::class.java)
                if (parsed != null) {
                    parsed.copy(id = doc.id, isApproved = approvedFlag)
                } else {
                    // Fallback: build minimal Place from known fields
                    val name = doc.getString("name") ?: ""
                    val category = doc.getString("category") ?: ""
                    val description = doc.getString("description") ?: ""
                    val address = doc.getString("address") ?: ""
                    val phone = doc.getString("phone") ?: ""
                    val openingTime = doc.getString("openingTime") ?: ""
                    val closingTime = doc.getString("closingTime") ?: ""
                    val rating =
                        doc.getDouble("rating") ?: (doc.get("rating") as? Number)?.toDouble() ?: 0.0

                    Place(
                        id = doc.id,
                        name = name,
                        category = category,
                        description = description,
                        address = address,
                        phone = phone,
                        openingTime = openingTime,
                        closingTime = closingTime,
                        rating = rating,
                        isApproved = approvedFlag
                    )
                }
            }

            Log.d("PlaceService", "Se obtuvieron ${places.size} lugares aprobados")
            Result.success(places)
        } catch (e: Exception) {
            Log.e("PlaceService", "Error al obtener lugares: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Obtiene lugares por categoría (solo aprobados).
     */
    suspend fun getPlacesByCategory(category: String): Result<List<Place>> {
        return try {
            if (category == "Todos") return getAllApprovedPlaces()

            val snapshot = placesCollection.get().await()

            val places = snapshot.documents.mapNotNull { doc ->
                val approvedFlag = (doc.getBoolean("approved") ?: doc.getBoolean("isApproved")) ?: false
                if (!approvedFlag) return@mapNotNull null
                val docCategory = doc.getString("category") ?: ""
                if (!docCategory.equals(category, ignoreCase = true)) return@mapNotNull null

                val parsed = doc.toObject(Place::class.java)
                if (parsed != null) parsed.copy(id = doc.id, isApproved = approvedFlag)
                else {
                    val name = doc.getString("name") ?: ""
                    val description = doc.getString("description") ?: ""
                    val address = doc.getString("address") ?: ""
                    val phone = doc.getString("phone") ?: ""
                    val openingTime = doc.getString("openingTime") ?: ""
                    val closingTime = doc.getString("closingTime") ?: ""
                    val rating = doc.getDouble("rating") ?: (doc.get("rating") as? Number)?.toDouble() ?: 0.0

                    Place(
                        id = doc.id,
                        name = name,
                        category = docCategory,
                        description = description,
                        address = address,
                        phone = phone,
                        openingTime = openingTime,
                        closingTime = closingTime,
                        rating = rating,
                        isApproved = approvedFlag
                    )
                }
            }

            Result.success(places)
        } catch (e: Exception) {
            Log.e("PlaceService", "Error al obtener lugares por categoría: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Busca lugares por nombre o categoría.
     */
    suspend fun searchPlaces(query: String, category: String = "Todos"): Result<List<Place>> {
        return try {
            Log.d("PlaceService", "Buscando lugares con query: $query, categoría: $category")

            // Primero obtener todos los lugares aprobados
            val allPlacesResult = getAllApprovedPlaces()

            if (allPlacesResult.isFailure) {
                return Result.failure(
                    allPlacesResult.exceptionOrNull() ?: Exception("Error desconocido")
                )
            }

            val allPlaces = allPlacesResult.getOrNull() ?: emptyList()

            // Filtrar por query y categoría localmente
            val filteredPlaces = allPlaces.filter { place ->
                val matchesQuery = query.isEmpty() ||
                        place.name.contains(query, ignoreCase = true) ||
                        place.description.contains(query, ignoreCase = true) ||
                        place.address.contains(query, ignoreCase = true)

                val matchesCategory = category == "Todos" ||
                        place.category.equals(category, ignoreCase = true)

                matchesQuery && matchesCategory
            }

            Log.d("PlaceService", "Se encontraron ${filteredPlaces.size} lugares")
            Result.success(filteredPlaces)
        } catch (e: Exception) {
            Log.e("PlaceService", "Error al buscar lugares: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Inserta lugares de prueba en Firebase (solo para desarrollo).
     */
    suspend fun insertSamplePlaces(): Result<String> {
        return try {
            Log.d("PlaceService", "Insertando lugares de prueba en Firebase")
            
            val samplePlaces = listOf(
                Place(
                    name = "Café Central",
                    category = "Cafetería",
                    description = "Acogedor café en el centro de la ciudad con excelente café artesanal y postres caseros.",
                    address = "Calle 10 # 15-20, Centro",
                    phone = "+57 300 123 4567",
                    openingTime = "07:00",
                    closingTime = "20:00",
                    workingDays = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"),
                    rating = 4.5,
                    latitude = 4.5389,
                    longitude = -75.6681,
                    isApproved = true
                ),
                Place(
                    name = "Restaurante El Fogón",
                    category = "Restaurantes",
                    description = "Restaurante tradicional con comida típica y ambiente familiar.",
                    address = "Carrera 8 # 12-45, Centro",
                    phone = "+57 310 234 5678",
                    openingTime = "11:00",
                    closingTime = "22:00",
                    workingDays = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"),
                    rating = 4.8,
                    latitude = 4.5395,
                    longitude = -75.6685,
                    isApproved = true
                ),
                Place(
                    name = "Hotel Plaza Real",
                    category = "Hoteles",
                    description = "Hotel boutique con habitaciones confortables.",
                    address = "Plaza Principal # 5-10",
                    phone = "+57 320 345 6789",
                    openingTime = "00:00",
                    closingTime = "23:59",
                    workingDays = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"),
                    rating = 4.3,
                    latitude = 4.5380,
                    longitude = -75.6670,
                    isApproved = true
                )
            )
            
            samplePlaces.forEach { place ->
                val docRef = placesCollection.document()
                val placeWithId = place.copy(id = docRef.id)
                docRef.set(placeWithId).await()
                Log.d("PlaceService", "Lugar insertado: ${place.name}")
            }
            
            Log.d("PlaceService", "${samplePlaces.size} lugares de prueba insertados correctamente")
            Result.success("${samplePlaces.size} lugares insertados")
        } catch (e: Exception) {
            Log.e("PlaceService", "Error al insertar lugares de prueba: ${e.message}")
            Result.failure(e)
        }
    }
}
