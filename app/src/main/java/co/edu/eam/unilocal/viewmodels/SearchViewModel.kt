package co.edu.eam.unilocal.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.eam.unilocal.models.Place
import co.edu.eam.unilocal.services.PlaceService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    
    private val placeService = PlaceService()
    
    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places: StateFlow<List<Place>> = _places.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        // Cargar todos los lugares al iniciar
        loadAllPlaces()
    }
    
    /**
     * Carga todos los lugares aprobados
     */
    fun loadAllPlaces() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val result = placeService.getAllApprovedPlaces()
                
                if (result.isSuccess) {
                    _places.value = result.getOrNull() ?: emptyList()
                    Log.d("SearchViewModel", "Lugares cargados: ${_places.value.size}")
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                    _errorMessage.value = error
                    Log.e("SearchViewModel", "Error al cargar lugares: $error")
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error desconocido"
                Log.e("SearchViewModel", "Excepción al cargar lugares: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Busca lugares por query y categoría
     */
    fun searchPlaces(query: String, category: String = "Todos") {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val result = placeService.searchPlaces(query, category)
                
                if (result.isSuccess) {
                    _places.value = result.getOrNull() ?: emptyList()
                    Log.d("SearchViewModel", "Búsqueda completada: ${_places.value.size} resultados")
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                    _errorMessage.value = error
                    Log.e("SearchViewModel", "Error en búsqueda: $error")
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error desconocido"
                Log.e("SearchViewModel", "Excepción en búsqueda: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Filtra lugares por categoría
     */
    fun filterByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val result = placeService.getPlacesByCategory(category)
                
                if (result.isSuccess) {
                    _places.value = result.getOrNull() ?: emptyList()
                    Log.d("SearchViewModel", "Filtrado por categoría: ${_places.value.size} resultados")
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                    _errorMessage.value = error
                    Log.e("SearchViewModel", "Error al filtrar: $error")
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error desconocido"
                Log.e("SearchViewModel", "Excepción al filtrar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _errorMessage.value = null
    }
}

