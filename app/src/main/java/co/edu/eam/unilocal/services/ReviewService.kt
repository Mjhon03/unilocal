package co.edu.eam.unilocal.services

import android.util.Log
import co.edu.eam.unilocal.models.Review
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ReviewService {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val reviewsCollection = firestore.collection("reviews")

    /**
     * Crear una nueva reseña
     */
    suspend fun createReview(review: Review): Result<Review> {
        return try {
            val newDocRef = reviewsCollection.document()
            val reviewWithId = review.copy(id = newDocRef.id)
            
            newDocRef.set(reviewWithId).await()
            Result.success(reviewWithId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener todas las reseñas de un lugar específico
     */
    suspend fun getReviewsByPlace(placeId: String): Result<List<Review>> {
        return try {
            // Ahora que el índice está creado, podemos usar orderBy para mejor rendimiento
            val snapshot = reviewsCollection
                .whereEqualTo("placeId", placeId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val reviews = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Review::class.java)?.copy(id = doc.id)
            }

            android.util.Log.d("ReviewService", "Cargadas ${reviews.size} reseñas para lugar $placeId (usando índice)")
            Result.success(reviews)
        } catch (e: Exception) {
            android.util.Log.e("ReviewService", "Error al cargar reseñas", e)
            Result.failure(e)
        }
    }

    /**
     * Obtener reseñas de un usuario específico
     */
    suspend fun getReviewsByUser(userId: String): Result<List<Review>> {
        return try {
            // Si necesitas esta consulta frecuentemente, podrías crear otro índice: userId + createdAt
            // Por ahora mantengo ordenamiento en cliente para esta consulta menos frecuente
            val snapshot = reviewsCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val reviews = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Review::class.java)?.copy(id = doc.id)
            }
            // Ordenar en el cliente por fecha descendente
            .sortedByDescending { it.createdAt }

            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Verificar si un usuario ya reseñó un lugar
     */
    suspend fun hasUserReviewedPlace(userId: String, placeId: String): Result<Boolean> {
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("placeId", placeId)
                .get()
                .await()

            Result.success(snapshot.documents.isNotEmpty())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualizar una reseña existente
     */
    suspend fun updateReview(review: Review): Result<Review> {
        return try {
            reviewsCollection.document(review.id).set(review).await()
            Result.success(review)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Eliminar una reseña
     */
    suspend fun deleteReview(reviewId: String): Result<Unit> {
        return try {
            reviewsCollection.document(reviewId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Crear reseñas de prueba para testing (solo para desarrollo)
     */
    suspend fun createTestReview(placeId: String): Result<Review> {
        val testReview = Review(
            placeId = placeId,
            userId = "test_user_${System.currentTimeMillis()}",
            userName = "Usuario de Prueba",
            userInitials = "UP",
            rating = (1..5).random(),
            comment = "Esta es una reseña de prueba generada automáticamente para verificar que el sistema funcione correctamente.",
            createdAt = System.currentTimeMillis()
        )
        
        return createReview(testReview)
    }
    
    /**
     * Calcular el rating promedio de un lugar
     */
    suspend fun calculateAverageRating(placeId: String): Result<Double> {
        return try {
            val reviewsResult = getReviewsByPlace(placeId)
            if (reviewsResult.isSuccess) {
                val reviews = reviewsResult.getOrNull() ?: emptyList()
                if (reviews.isNotEmpty()) {
                    val average = reviews.map { it.rating }.average()
                    Result.success(average)
                } else {
                    Result.success(0.0)
                }
            } else {
                Result.success(0.0)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}