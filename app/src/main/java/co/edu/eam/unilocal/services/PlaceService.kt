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
                .whereEqualTo("approved", true)
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
                .whereEqualTo("approved", true)
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
}
