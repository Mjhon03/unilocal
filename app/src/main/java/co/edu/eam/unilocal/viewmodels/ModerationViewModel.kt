package co.edu.eam.unilocal.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.eam.unilocal.models.ModerationPlace
import co.edu.eam.unilocal.services.PlaceService
import co.edu.eam.unilocal.services.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ModerationViewModel : ViewModel() {
    private val placeService = PlaceService()
    // keep simple: no userService name resolution to avoid added complexity

    private val _pending = MutableStateFlow<List<ModerationPlace>>(emptyList())
    val pending: StateFlow<List<ModerationPlace>> = _pending.asStateFlow()

    private val _history = MutableStateFlow<List<ModerationPlace>>(emptyList())
    val history: StateFlow<List<ModerationPlace>> = _history.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    var currentModeratorId: String? = null
    var currentModeratorName: String? = null

    fun setModerator(userId: String, name: String) {
        currentModeratorId = userId
        currentModeratorName = name
    }

    init {
        loadPending()
        // loadHistory() se debe llamar después de setModerator
    }

    fun loadPending() {
        viewModelScope.launch {
            try {
                Log.d("ModerationVM", "=== CARGANDO LUGARES PENDIENTES ===")
                _isLoading.value = true
                _error.value = null
                
                val result = placeService.getPendingModerationPlaces()
                
                if (result.isSuccess) {
                    val places = result.getOrNull() ?: emptyList()
                    _pending.value = places
                    Log.d("ModerationVM", "✓ Lugares cargados exitosamente: ${places.size}")
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Error desconocido"
                    _error.value = errorMsg
                    Log.e("ModerationVM", "✗ Error al cargar lugares: $errorMsg")
                }
            } catch (e: Exception) {
                Log.e("ModerationVM", "✗ Excepción al cargar pendings: ${e.message}")
                e.printStackTrace()
                _error.value = e.message
            } finally {
                _isLoading.value = false
                Log.d("ModerationVM", "=== FIN CARGA (isLoading=false) ===")
            }
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val moderatorId = currentModeratorId
                if (moderatorId.isNullOrBlank()) {
                    _history.value = emptyList()
                    _isLoading.value = false
                    return@launch
                }
                val result = placeService.getModerationHistory(moderatorId)
                if (result.isSuccess) {
                    _history.value = result.getOrNull() ?: emptyList()
                } else {
                    _error.value = result.exceptionOrNull()?.message
                }
            } catch (e: Exception) {
                Log.e("ModerationVM", "Error cargando history: ${e.message}")
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun approve(moderationId: String, onComplete: (Boolean, String?) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val moderatorId = currentModeratorId
                val moderatorName = currentModeratorName
                if (moderatorId.isNullOrBlank() || moderatorName.isNullOrBlank()) {
                    onComplete(false, "Moderador no definido")
                    _isLoading.value = false
                    return@launch
                }
                val result = placeService.approveModerationPlace(moderationId, moderatorId, moderatorName)
                if (result.isSuccess) {
                    loadPending()
                    loadHistory()
                    onComplete(true, result.getOrNull())
                } else {
                    onComplete(false, result.exceptionOrNull()?.message)
                }
            } catch (e: Exception) {
                onComplete(false, e.message)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun reject(moderationId: String, onComplete: (Boolean, String?) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val moderatorId = currentModeratorId
                val moderatorName = currentModeratorName
                if (moderatorId.isNullOrBlank() || moderatorName.isNullOrBlank()) {
                    onComplete(false, "Moderador no definido")
                    _isLoading.value = false
                    return@launch
                }
                val result = placeService.rejectModerationPlace(moderationId, moderatorId, moderatorName)
                if (result.isSuccess) {
                    loadPending()
                    loadHistory()
                    onComplete(true, null)
                } else {
                    onComplete(false, result.exceptionOrNull()?.message)
                }
            } catch (e: Exception) {
                onComplete(false, e.message)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
