package co.edu.eam.unilocal.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.eam.unilocal.models.Place
import co.edu.eam.unilocal.services.PlaceService
import co.edu.eam.unilocal.services.UserService
import co.edu.eam.unilocal.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class PlacesViewModel : ViewModel() {
    
    private val placeService = PlaceService()
    
    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places: StateFlow<List<Place>> = _places.asStateFlow()
    
    // Favoritos por usuario: Map<userId, Set<placeId>>
    private val _userFavorites = MutableStateFlow<Map<String, Set<String>>>(emptyMap())
    private val _currentUserFavorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _currentUserFavorites.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<Place>>(emptyList())
    val searchResults: StateFlow<List<Place>> = _searchResults.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private var currentUserId: String? = null
    private val userService = UserService()
    
    init {
        // Cargar lugares desde Firebase
        loadPlacesFromFirebase()
    }
    
    private fun loadPlacesFromFirebase() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                Log.d("PlacesViewModel", "Cargando lugares desde Firebase")
                
                val result = placeService.getAllApprovedPlaces()
                
                if (result.isSuccess) {
                    var places = result.getOrNull() ?: emptyList()
                    
                    // Si no hay lugares, insertar datos de prueba
                    if (places.isEmpty()) {
                        Log.d("PlacesViewModel", "No hay lugares en Firebase, insertando datos de prueba...")
                        val insertResult = placeService.insertSamplePlaces()
                        
                        if (insertResult.isSuccess) {
                            Log.d("PlacesViewModel", "Datos de prueba insertados, recargando...")
                            // Recargar después de insertar
                            val reloadResult = placeService.getAllApprovedPlaces()
                            if (reloadResult.isSuccess) {
                                places = reloadResult.getOrNull() ?: emptyList()
                            }
                        }
                    }
                    
                    _places.value = places
                    _searchResults.value = places
                    Log.d("PlacesViewModel", "Se cargaron ${places.size} lugares desde Firebase")
                } else {
                    _errorMessage.value = "Error al cargar lugares"
                    Log.e("PlacesViewModel", "Error al cargar lugares: ${result.exceptionOrNull()?.message}")
                }
                
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error desconocido"
                Log.e("PlacesViewModel", "Error al cargar lugares: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun refreshPlaces() {
        loadPlacesFromFirebase()
    }
    
    fun addPlace(place: Place) {
        val newPlace = place.copy(
            id = UUID.randomUUID().toString(),
            createdAt = System.currentTimeMillis()
        )
        _places.value = _places.value + newPlace
        _searchResults.value = _places.value
    }
    
    // Establecer el usuario actual y cargar sus favoritos
    fun setCurrentUser(userId: String?) {
        currentUserId = userId
        if (userId != null) {
            // Cargar favoritos del usuario
            // Try to load favorites from Firestore user document if not loaded locally
            viewModelScope.launch {
                try {
                    val result = userService.getUserById(userId)
                    if (result.isSuccess) {
                        val user = result.getOrNull()
                        if (user != null) {
                            _currentUserFavorites.value = user.favorites.toSet()
                        } else {
                            _currentUserFavorites.value = _userFavorites.value[userId] ?: emptySet()
                        }
                    } else {
                        _currentUserFavorites.value = _userFavorites.value[userId] ?: emptySet()
                    }
                } catch (e: Exception) {
                    _currentUserFavorites.value = _userFavorites.value[userId] ?: emptySet()
                }
            }
        } else {
            // Limpiar favoritos si no hay usuario
            _currentUserFavorites.value = emptySet()
        }
    }
    
    fun toggleFavorite(placeId: String, onComplete: (Boolean, String?) -> Unit = { _, _ -> }) {
        val userId = currentUserId
        if (userId == null) {
            android.util.Log.w("PlacesViewModel", "toggleFavorite called but no current user")
            onComplete(false, "not_authenticated")
            return
        }

        val currentUserFavs = _currentUserFavorites.value
        val newUserFavs = if (currentUserFavs.contains(placeId)) {
            currentUserFavs - placeId
        } else {
            currentUserFavs + placeId
        }

        android.util.Log.d("PlacesViewModel", "User $userId favorites updating. Before=${currentUserFavs.size}, After=${newUserFavs.size}")

        // Actualizar favoritos del usuario actual (optimistic)
        _currentUserFavorites.value = newUserFavs

        // Actualizar en el mapa global
        val allFavorites = _userFavorites.value.toMutableMap()
        allFavorites[userId] = newUserFavs
        _userFavorites.value = allFavorites

        // Persistir favoritos en Firestore para el usuario actual
        viewModelScope.launch {
            try {
                // Obtener usuario y actualizar su lista de favoritos
                val userResult = userService.getUserById(userId)
                if (userResult.isSuccess) {
                    val user = userResult.getOrNull()
                    if (user != null) {
                        val updatedUser = user.copy(favorites = newUserFavs.toList())
                        val updateResult = userService.updateUser(updatedUser)
                        if (updateResult.isFailure) {
                            val msg = updateResult.exceptionOrNull()?.message
                            // Log but don't revert UI
                            android.util.Log.e("PlacesViewModel", "No se pudo persistir favoritos: $msg")
                            onComplete(false, msg)
                        } else {
                            android.util.Log.d("PlacesViewModel", "Favoritos persistidos correctamente para user $userId")
                            onComplete(true, null)
                        }
                    } else {
                        // User doc missing
                        android.util.Log.e("PlacesViewModel", "User doc not found when persisting favorites for $userId")
                        onComplete(false, "user_not_found")
                    }
                } else {
                    val msg = userResult.exceptionOrNull()?.message
                    android.util.Log.e("PlacesViewModel", "Error fetching user for favorites: $msg")
                    onComplete(false, msg)
                }
            } catch (e: Exception) {
                android.util.Log.e("PlacesViewModel", "Error persisting favorites: ${e.message}")
                onComplete(false, e.message)
            }
        }
    }
    
    fun isFavorite(placeId: String): Boolean {
        return _currentUserFavorites.value.contains(placeId)
    }
    
    fun getFavoritePlaces(): List<Place> {
        return _places.value.filter { it.id in _currentUserFavorites.value }
    }
    
    fun getUserPlaces(userId: String): List<Place> {
        return _places.value.filter { it.createdBy == userId }
    }
    
    fun clearUserData() {
        currentUserId = null
        _currentUserFavorites.value = emptySet()
    }
    
    fun searchPlaces(query: String, category: String = "Todos") {
        viewModelScope.launch {
            try {
                // Si no hay query ni categoría específica y ya tenemos datos, usar los locales
                if (query.isBlank() && category == "Todos" && _places.value.isNotEmpty()) {
                    _searchResults.value = _places.value
                    Log.d("PlacesViewModel", "Usando lugares cargados: ${_places.value.size} lugares")
                    return@launch
                }
                
                _isLoading.value = true
                _errorMessage.value = null
                
                Log.d("PlacesViewModel", "Buscando lugares: query='$query', category='$category'")
                
                val result = placeService.searchPlaces(query, category)
                
                if (result.isSuccess) {
                    val places = result.getOrNull() ?: emptyList()
                    _searchResults.value = places
                    Log.d("PlacesViewModel", "Se encontraron ${places.size} lugares")
                } else {
                    _errorMessage.value = "Error al buscar lugares"
                    Log.e("PlacesViewModel", "Error al buscar: ${result.exceptionOrNull()?.message}")
                }
                
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error desconocido"
                Log.e("PlacesViewModel", "Error en búsqueda: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun getPlaceById(placeId: String): Place? {
        return _places.value.find { it.id == placeId }
    }
}

