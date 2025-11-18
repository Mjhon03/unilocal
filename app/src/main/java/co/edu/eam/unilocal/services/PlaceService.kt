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
    private val moderationPlacesCollection = firestore.collection("moderation_places")

    /**
     * Crea una entrada de moderación usando la colección `moderation_places`.
     * Devuelve Result.success(documentId) si se creó correctamente.
     */
    suspend fun createModerationPlace(place: ModerationPlace): Result<String> {
        return try {
            Log.d("PlaceService", "Creando lugar para moderación: ${place.name}")

            // Generar id
            val newDocRef = moderationPlacesCollection.document()
            
            val placeWithId = place.copy(id = newDocRef.id)

            // Guardar en moderation_places
            newDocRef.set(placeWithId).await()

            Log.d("PlaceService", "Lugar creado en moderation_places: ${newDocRef.id}")
            Result.success(newDocRef.id)
        } catch (e: Exception) {
            Log.e("PlaceService", "Error al crear lugar en moderation_places: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Obtiene los lugares pendientes desde la colección `moderation_places`.
     * Solo devuelve lugares con estado PENDING.
     */
    suspend fun getPendingModerationPlaces(): Result<List<ModerationPlace>> {
        return try {
            Log.d("PlaceService", "=== INICIANDO CARGA DE LUGARES PENDIENTES ===")
            Log.d("PlaceService", "Colección: moderation_places")
            Log.d("PlaceService", "Filtro: status = ${ModerationStatus.PENDING.name}")
            
            val snapshot = moderationPlacesCollection
                .whereEqualTo("status", ModerationStatus.PENDING.name)
                .get()
                .await()

            Log.d("PlaceService", "Documentos encontrados en query: ${snapshot.documents.size}")
            
            val places = snapshot.documents.mapNotNull { doc ->
                try {
                    Log.d("PlaceService", "Procesando documento: ${doc.id}")
                    Log.d("PlaceService", "  - name: ${doc.getString("name")}")
                    Log.d("PlaceService", "  - status: ${doc.getString("status")}")
                    Log.d("PlaceService", "  - address: ${doc.getString("address")}")
                    
                    val place = doc.toObject(ModerationPlace::class.java)
                    Log.d("PlaceService", "  ✓ Documento parseado exitosamente")
                    place
                } catch (e: Exception) {
                    Log.e("PlaceService", "  ✗ Error parseando documento ${doc.id}: ${e.message}")
                    e.printStackTrace()
                    null
                }
            }
            
            Log.d("PlaceService", "=== RESULTADO: ${places.size} lugares pendientes ===")
            places.forEach { place ->
                Log.d("PlaceService", "  - ${place.name} (${place.id})")
            }
            
            Result.success(places)
        } catch (e: Exception) {
            Log.e("PlaceService", "=== ERROR AL OBTENER LUGARES PENDIENTES ===")
            Log.e("PlaceService", "Mensaje: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Rechaza una moderación actualizando su estado a REJECTED en moderation_places.
     */
    suspend fun rejectModerationPlace(
        moderationId: String,
        moderatorId: String,
        moderatorName: String
    ): Result<Unit> {
        return try {
            Log.d("PlaceService", "Rechazando lugar: $moderationId por $moderatorName ($moderatorId)")
            moderationPlacesCollection.document(moderationId).update(
                mapOf(
                    "status" to ModerationStatus.REJECTED.name,
                    "moderatedBy" to moderatorId,
                    "moderatedByName" to moderatorName,
                    "moderatedAt" to System.currentTimeMillis()
                )
            ).await()
            Log.d("PlaceService", "Lugar rechazado exitosamente")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("PlaceService", "Error al rechazar lugar: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Aprueba una moderación: actualiza el estado en moderation_places y crea el lugar en places.
     */
    suspend fun approveModerationPlace(
        moderationId: String,
        moderatorId: String,
        moderatorName: String
    ): Result<String> {
        return try {
            Log.d("PlaceService", "Aprobando lugar: $moderationId por $moderatorName ($moderatorId)")
            // 1. Obtener el lugar de moderation_places
            val moderationDoc = moderationPlacesCollection.document(moderationId).get().await()
            if (!moderationDoc.exists()) {
                return Result.failure(Exception("Lugar no encontrado en moderation_places"))
            }
            val moderationPlace = moderationDoc.toObject(ModerationPlace::class.java)
                ?: return Result.failure(Exception("Error al parsear lugar"))
            // 2. Crear el lugar aprobado en la colección places
            val newPlaceRef = placesCollection.document()
            val createdAtLong = moderationPlace.createdAt.toLongOrNull() ?: System.currentTimeMillis()
            val approvedPlace = Place(
                id = newPlaceRef.id,
                name = moderationPlace.name,
                category = moderationPlace.category,
                description = moderationPlace.description,
                address = moderationPlace.address,
                phone = moderationPlace.phone ?: "",
                openingTime = moderationPlace.openingTime,
                closingTime = moderationPlace.closingTime,
                workingDays = moderationPlace.workingDays,
                photoUrls = moderationPlace.photoUrls,
                latitude = moderationPlace.latitude,
                longitude = moderationPlace.longitude,
                rating = 0.0,
                createdBy = moderationPlace.submittedBy,
                createdAt = createdAtLong,
                isApproved = true
            )
            newPlaceRef.set(approvedPlace).await()
            // 3. Actualizar el estado y auditoría en moderation_places
            moderationPlacesCollection.document(moderationId).update(
                mapOf(
                    "status" to ModerationStatus.APPROVED.name,
                    "moderatedBy" to moderatorId,
                    "moderatedByName" to moderatorName,
                    "moderatedAt" to System.currentTimeMillis()
                )
            ).await()
            Log.d("PlaceService", "Lugar aprobado exitosamente. ID en places: ${newPlaceRef.id}")
            Result.success(newPlaceRef.id)
        } catch (e: Exception) {
            Log.e("PlaceService", "Error al aprobar lugar: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Obtiene el historial de moderación: lugares aprobados y rechazados.
     */
    suspend fun getModerationHistory(moderatorId: String): Result<List<ModerationPlace>> {
        return try {
            Log.d("PlaceService", "Obteniendo historial de moderación para moderador: $moderatorId")
            val snapshot = moderationPlacesCollection
                .whereEqualTo("moderatedBy", moderatorId)
                .get().await()
            val places = snapshot.documents.mapNotNull { doc ->
                try {
                    val moderationPlace = doc.toObject(ModerationPlace::class.java)
                    // Filtrar manualmente para excluir PENDING
                    if (moderationPlace != null && moderationPlace.status != ModerationStatus.PENDING) {
                        moderationPlace
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    Log.e("PlaceService", "Error parseando historial ${doc.id}: ${e.message}")
                    null
                }
            }
            Log.d("PlaceService", "Historial obtenido: ${places.size} lugares")
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
                    val address = doc.getString("address") ?: "Armenia, Quindío"
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
                        workingDays = (doc.get("workingDays") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                        photoUrls = (doc.get("photoUrls") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                        latitude = doc.getDouble("latitude") ?: 0.0,
                        longitude = doc.getDouble("longitude") ?: 0.0,
                        rating = rating,
                        createdBy = doc.getString("createdBy") ?: "",
                        createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
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
                    val address = doc.getString("address") ?: "Armenia, Quindío"
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
                        workingDays = (doc.get("workingDays") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                        photoUrls = (doc.get("photoUrls") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                        latitude = doc.getDouble("latitude") ?: 0.0,
                        longitude = doc.getDouble("longitude") ?: 0.0,
                        rating = rating,
                        createdBy = doc.getString("createdBy") ?: "",
                        createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
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
     * Obtiene un lugar específico por ID.
     */
    suspend fun getPlaceById(placeId: String): Result<Place?> {
        return try {
            val snapshot = placesCollection.document(placeId).get().await()
            
            if (!snapshot.exists()) {
                return Result.success(null)
            }
            
            val approvedFlag = (snapshot.getBoolean("approved") ?: snapshot.getBoolean("isApproved")) ?: false
            if (!approvedFlag) {
                return Result.success(null)
            }
            
                // Mapear los datos de Firebase al modelo Place
            val place = Place(
                id = snapshot.id,
                name = snapshot.getString("name") ?: "",
                category = snapshot.getString("category") ?: "",
                description = snapshot.getString("description") ?: "",
                address = snapshot.getString("address") ?: "Armenia, Quindío", // Usar address de Firebase
                phone = snapshot.getString("phone") ?: "",
                openingTime = snapshot.getString("openingTime") ?: "",
                closingTime = snapshot.getString("closingTime") ?: "",
                workingDays = (snapshot.get("workingDays") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                photoUrls = (snapshot.get("photoUrls") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                latitude = snapshot.getDouble("latitude") ?: 0.0,
                longitude = snapshot.getDouble("longitude") ?: 0.0,
                rating = snapshot.getDouble("rating") ?: 0.0,
                createdBy = snapshot.getString("createdBy") ?: "",
                createdAt = snapshot.getLong("createdAt") ?: System.currentTimeMillis(),
                isApproved = approvedFlag
            )
            
            Result.success(place)
        } catch (e: Exception) {
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
