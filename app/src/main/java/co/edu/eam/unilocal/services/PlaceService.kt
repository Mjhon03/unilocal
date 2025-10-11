package co.edu.eam.unilocal.services

import android.util.Log
import co.edu.eam.unilocal.models.ModerationPlace
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PlaceService {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val moderationCollection = firestore.collection("moderation_places")

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
}
