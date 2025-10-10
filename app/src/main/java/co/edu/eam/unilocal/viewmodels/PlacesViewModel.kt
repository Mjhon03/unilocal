package co.edu.eam.unilocal.viewmodels

import androidx.lifecycle.ViewModel
import co.edu.eam.unilocal.models.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class PlacesViewModel : ViewModel() {
    
    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places: StateFlow<List<Place>> = _places.asStateFlow()
    
    // Favoritos por usuario: Map<userId, Set<placeId>>
    private val _userFavorites = MutableStateFlow<Map<String, Set<String>>>(emptyMap())
    private val _currentUserFavorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _currentUserFavorites.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<Place>>(emptyList())
    val searchResults: StateFlow<List<Place>> = _searchResults.asStateFlow()
    
    private var currentUserId: String? = null
    
    init {
        // Crear 10 lugares de ejemplo
        initializeSamplePlaces()
    }
    
    private fun initializeSamplePlaces() {
        val samplePlaces = listOf(
            Place(
                id = UUID.randomUUID().toString(),
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
                id = UUID.randomUUID().toString(),
                name = "Restaurante El Fogón",
                category = "Restaurantes",
                description = "Restaurante tradicional con comida típica y ambiente familiar. Especialidad en bandeja paisa.",
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
                id = UUID.randomUUID().toString(),
                name = "Hotel Plaza Real",
                category = "Hoteles",
                description = "Hotel boutique con habitaciones confortables y vista a la plaza principal.",
                address = "Plaza Principal # 5-10",
                phone = "+57 320 345 6789",
                openingTime = "00:00",
                closingTime = "23:59",
                workingDays = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"),
                rating = 4.3,
                latitude = 4.5380,
                longitude = -75.6670,
                isApproved = true
            ),
            Place(
                id = UUID.randomUUID().toString(),
                name = "Museo de Arte Moderno",
                category = "Museos",
                description = "Museo con colección permanente y exposiciones temporales de arte contemporáneo.",
                address = "Avenida Cultural # 20-30",
                phone = "+57 330 456 7890",
                openingTime = "09:00",
                closingTime = "18:00",
                workingDays = listOf("Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"),
                rating = 4.6,
                latitude = 4.5400,
                longitude = -75.6690,
                isApproved = true
            ),
            Place(
                id = UUID.randomUUID().toString(),
                name = "Café Literario",
                category = "Cafetería",
                description = "Espacio cultural con café, librería y eventos literarios semanales.",
                address = "Carrera 15 # 8-25",
                phone = "+57 340 567 8901",
                openingTime = "08:00",
                closingTime = "21:00",
                workingDays = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"),
                rating = 4.7,
                latitude = 4.5385,
                longitude = -75.6675,
                isApproved = true
            ),
            Place(
                id = UUID.randomUUID().toString(),
                name = "Pizzería Italiana",
                category = "Restaurantes",
                description = "Auténtica pizzería italiana con horno de leña y recetas tradicionales.",
                address = "Calle 14 # 10-15",
                phone = "+57 350 678 9012",
                openingTime = "12:00",
                closingTime = "23:00",
                workingDays = listOf("Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"),
                rating = 4.9,
                latitude = 4.5392,
                longitude = -75.6682,
                isApproved = true
            ),
            Place(
                id = UUID.randomUUID().toString(),
                name = "Hotel Boutique La Casona",
                category = "Hoteles",
                description = "Casa colonial restaurada convertida en hotel de lujo con piscina y spa.",
                address = "Calle Colonial # 3-20",
                phone = "+57 360 789 0123",
                openingTime = "00:00",
                closingTime = "23:59",
                workingDays = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"),
                rating = 4.8,
                latitude = 4.5388,
                longitude = -75.6678,
                isApproved = true
            ),
            Place(
                id = UUID.randomUUID().toString(),
                name = "Galería de Historia Natural",
                category = "Museos",
                description = "Museo interactivo con exposiciones sobre biodiversidad y ecosistemas locales.",
                address = "Parque Natural # 25-40",
                phone = "+57 370 890 1234",
                openingTime = "10:00",
                closingTime = "17:00",
                workingDays = listOf("Mié", "Jue", "Vie", "Sáb", "Dom"),
                rating = 4.4,
                latitude = 4.5398,
                longitude = -75.6688,
                isApproved = true
            ),
            Place(
                id = UUID.randomUUID().toString(),
                name = "Café del Parque",
                category = "Cafetería",
                description = "Cafetería al aire libre con terraza y vista al parque principal.",
                address = "Parque Principal # 1-5",
                phone = "+57 380 901 2345",
                openingTime = "06:30",
                closingTime = "19:30",
                workingDays = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"),
                rating = 4.2,
                latitude = 4.5382,
                longitude = -75.6672,
                isApproved = true
            ),
            Place(
                id = UUID.randomUUID().toString(),
                name = "Sushi Bar Tokio",
                category = "Restaurantes",
                description = "Restaurante japonés con sushi fresco y platillos asiáticos contemporáneos.",
                address = "Zona Rosa # 18-30",
                phone = "+57 390 012 3456",
                openingTime = "12:30",
                closingTime = "22:30",
                workingDays = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"),
                rating = 4.7,
                latitude = 4.5396,
                longitude = -75.6686,
                isApproved = true
            )
        )
        
        _places.value = samplePlaces
        _searchResults.value = samplePlaces
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
            _currentUserFavorites.value = _userFavorites.value[userId] ?: emptySet()
        } else {
            // Limpiar favoritos si no hay usuario
            _currentUserFavorites.value = emptySet()
        }
    }
    
    fun toggleFavorite(placeId: String) {
        val userId = currentUserId ?: return
        
        val currentUserFavs = _currentUserFavorites.value
        val newUserFavs = if (currentUserFavs.contains(placeId)) {
            currentUserFavs - placeId
        } else {
            currentUserFavs + placeId
        }
        
        // Actualizar favoritos del usuario actual
        _currentUserFavorites.value = newUserFavs
        
        // Actualizar en el mapa global
        val allFavorites = _userFavorites.value.toMutableMap()
        allFavorites[userId] = newUserFavs
        _userFavorites.value = allFavorites
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
        if (query.isBlank() && category == "Todos") {
            _searchResults.value = _places.value
            return
        }
        
        var results = _places.value
        
        // Filtrar por categoría
        if (category != "Todos") {
            results = results.filter { 
                it.category.equals(category, ignoreCase = true) 
            }
        }
        
        // Filtrar por búsqueda
        if (query.isNotBlank()) {
            results = results.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true) ||
                it.address.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true)
            }
        }
        
        _searchResults.value = results
    }
    
    fun getPlaceById(placeId: String): Place? {
        return _places.value.find { it.id == placeId }
    }
}

