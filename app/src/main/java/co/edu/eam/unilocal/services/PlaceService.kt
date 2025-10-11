package co.edu.eam.unilocal.services

import android.util.Log
import co.edu.eam.unilocal.models.ModerationPlace
import co.edu.eam.unilocal.models.Place
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PlaceService {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val moderationCollection = firestore.collection("moderation_places")
    private val placesCollection = firestore.collection("places")

    /**
     * Crea un ModerationPlace en la colección `moderation_places`.
     * Devuelve Result.success(documentId) si se creó correctamente.
     */
    suspend fun createModerationPlace(place: ModerationPlace): Result<String> {
        return try {
            Log.d("PlaceService", "Creando lugar para moderación: ${place.name}")

            // Generar id temporal
            val docRef = moderationCollection.document()
            val placeWithId = place.copy(id = docRef.id)

            docRef.set(placeWithId).await()

            Log.d("PlaceService", "Lugar creado para moderación: ${docRef.id}")
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("PlaceService", "Error al crear lugar: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Obtiene todos los lugares aprobados desde la colección `places`.
     */
    suspend fun getAllApprovedPlaces(): Result<List<Place>> {
        return try {
            Log.d("PlaceService", "Obteniendo todos los lugares aprobados")
            
            val snapshot = placesCollection
                .whereEqualTo("isApproved", true)
                .get()
                .await()
            
            val places = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Place::class.java)?.copy(id = doc.id)
            }
            
            Log.d("PlaceService", "Se obtuvieron ${places.size} lugares aprobados")
            Result.success(places)
        } catch (e: Exception) {
            Log.e("PlaceService", "Error al obtener lugares: ${e.message}")
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
                return Result.failure(allPlacesResult.exceptionOrNull() ?: Exception("Error desconocido"))
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
     * Obtiene lugares por categoría.
     */
    suspend fun getPlacesByCategory(category: String): Result<List<Place>> {
        return try {
            Log.d("PlaceService", "Obteniendo lugares de la categoría: $category")
            
            if (category == "Todos") {
                return getAllApprovedPlaces()
            }
            
            val snapshot = placesCollection
                .whereEqualTo("isApproved", true)
                .whereEqualTo("category", category)
                .get()
                .await()
            
            val places = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Place::class.java)?.copy(id = doc.id)
            }
            
            Log.d("PlaceService", "Se obtuvieron ${places.size} lugares de la categoría $category")
            Result.success(places)
        } catch (e: Exception) {
            Log.e("PlaceService", "Error al obtener lugares por categoría: ${e.message}")
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
