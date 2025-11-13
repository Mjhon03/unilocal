package co.edu.eam.unilocal.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import co.edu.eam.unilocal.models.Place
import co.edu.eam.unilocal.services.PlaceService

class SharedPlaceViewModel : ViewModel() {
    
    private val placeService = PlaceService()
    
    private val _selectedPlace = MutableStateFlow<Place?>(null)
    val selectedPlace: StateFlow<Place?> = _selectedPlace.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    fun setSelectedPlace(place: Place) {
        _selectedPlace.value = place
        _errorMessage.value = null
    }
    
    fun loadPlaceById(placeId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                

                
                val result = placeService.getPlaceById(placeId)
                
                if (result.isSuccess) {
                    val place = result.getOrNull()
                    if (place != null) {
                        _selectedPlace.value = place
                    } else {
                        _errorMessage.value = "Lugar no encontrado"
                    }
                } else {
                    _errorMessage.value = "Error al cargar lugar"
                }
                
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error desconocido"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearSelectedPlace() {
        _selectedPlace.value = null
        _errorMessage.value = null
    }
}