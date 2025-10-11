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

    init {
        loadPending()
        loadHistory()
    }

    fun loadPending() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = placeService.getPendingModerationPlaces()
                if (result.isSuccess) {
                    _pending.value = result.getOrNull() ?: emptyList()
                } else {
                    _error.value = result.exceptionOrNull()?.message
                }
            } catch (e: Exception) {
                Log.e("ModerationVM", "Error cargando pendings: ${e.message}")
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                    val result = placeService.getModerationHistory()
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
                val result = placeService.approveModerationPlace(moderationId)
                if (result.isSuccess) {
                    // Reload pending and history
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
                val result = placeService.rejectModerationPlace(moderationId)
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
