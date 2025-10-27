package co.edu.eam.unilocal.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.eam.unilocal.models.Review
import co.edu.eam.unilocal.services.ReviewService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReviewViewModel : ViewModel() {
    
    private val reviewService = ReviewService()
    
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _averageRating = MutableStateFlow(0.0)
    val averageRating: StateFlow<Double> = _averageRating.asStateFlow()
    
    private val _hasUserReviewed = MutableStateFlow(false)
    val hasUserReviewed: StateFlow<Boolean> = _hasUserReviewed.asStateFlow()
    
    /**
     * Cargar reseñas de un lugar específico
     */
    fun loadReviewsForPlace(placeId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                android.util.Log.d("ReviewViewModel", "Cargando reseñas para lugar: $placeId")
                
                val result = reviewService.getReviewsByPlace(placeId)
                
                if (result.isSuccess) {
                    val reviews = result.getOrNull() ?: emptyList()
                    _reviews.value = reviews
                    
                    android.util.Log.d("ReviewViewModel", "Cargadas ${reviews.size} reseñas")
                    
                    // Calcular rating promedio
                    val ratingResult = reviewService.calculateAverageRating(placeId)
                    if (ratingResult.isSuccess) {
                        val avgRating = ratingResult.getOrNull() ?: 0.0
                        _averageRating.value = avgRating
                        android.util.Log.d("ReviewViewModel", "Rating promedio: $avgRating")
                    }
                } else {
                    android.util.Log.e("ReviewViewModel", "Error al cargar reseñas: ${result.exceptionOrNull()?.message}")
                    _errorMessage.value = "Error al cargar reseñas"
                }
                
            } catch (e: Exception) {
                android.util.Log.e("ReviewViewModel", "Excepción al cargar reseñas", e)
                _errorMessage.value = e.message ?: "Error desconocido"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Verificar si el usuario ya reseñó el lugar
     */
    fun checkUserReviewed(userId: String, placeId: String) {
        viewModelScope.launch {
            try {
                val result = reviewService.hasUserReviewedPlace(userId, placeId)
                if (result.isSuccess) {
                    _hasUserReviewed.value = result.getOrNull() ?: false
                }
            } catch (e: Exception) {
                _hasUserReviewed.value = false
            }
        }
    }
    
    /**
     * Crear una nueva reseña
     */
    fun createReview(
        placeId: String,
        userId: String,
        userName: String,
        rating: Int,
        comment: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                android.util.Log.d("ReviewViewModel", "Creando reseña para lugar: $placeId")
                
                // Generar iniciales del nombre
                val initials = userName.split(" ")
                    .take(2)
                    .map { it.firstOrNull()?.uppercaseChar() ?: "" }
                    .joinToString("")
                
                val review = Review(
                    placeId = placeId,
                    userId = userId,
                    userName = userName,
                    userInitials = initials,
                    rating = rating,
                    comment = comment
                )
                
                val result = reviewService.createReview(review)
                
                if (result.isSuccess) {
                    android.util.Log.d("ReviewViewModel", "Reseña creada exitosamente, recargando lista...")
                    
                    // Marcar que el usuario ya reseñó
                    _hasUserReviewed.value = true
                    
                    // Recargar reseñas inmediatamente
                    val reloadResult = reviewService.getReviewsByPlace(placeId)
                    if (reloadResult.isSuccess) {
                        val updatedReviews = reloadResult.getOrNull() ?: emptyList()
                        _reviews.value = updatedReviews
                        android.util.Log.d("ReviewViewModel", "Lista actualizada con ${updatedReviews.size} reseñas")
                        
                        // Recalcular rating promedio
                        val avgResult = reviewService.calculateAverageRating(placeId)
                        if (avgResult.isSuccess) {
                            _averageRating.value = avgResult.getOrNull() ?: 0.0
                        }
                    }
                    
                    onComplete(true, "Reseña creada exitosamente")
                } else {
                    android.util.Log.e("ReviewViewModel", "Error al crear reseña: ${result.exceptionOrNull()?.message}")
                    _errorMessage.value = "Error al crear reseña"
                    onComplete(false, "Error al crear reseña")
                }
                
            } catch (e: Exception) {
                android.util.Log.e("ReviewViewModel", "Excepción al crear reseña", e)
                _errorMessage.value = e.message ?: "Error desconocido"
                onComplete(false, e.message)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Refrescar completamente los datos de un lugar
     */
    fun refreshPlaceData(placeId: String, userId: String? = null) {
        viewModelScope.launch {
            android.util.Log.d("ReviewViewModel", "Refrescando datos completos para lugar: $placeId")
            
            // Cargar reseñas
            loadReviewsForPlace(placeId)
            
            // Verificar si el usuario ya reseñó (si está autenticado)
            userId?.let { id ->
                checkUserReviewed(id, placeId)
            }
        }
    }
    
    /**
     * Limpiar estado
     */
    fun clearState() {
        _reviews.value = emptyList()
        _averageRating.value = 0.0
        _hasUserReviewed.value = false
        _errorMessage.value = null
    }
}