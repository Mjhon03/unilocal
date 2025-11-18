package co.edu.eam.unilocal.viewmodels

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class LocationState(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasPermission: Boolean = false
)

class LocationViewModel : ViewModel() {
    
    private val _locationState = MutableStateFlow(LocationState())
    val locationState: StateFlow<LocationState> = _locationState.asStateFlow()
    
    private var fusedLocationClient: FusedLocationProviderClient? = null
    
    fun initializeLocationClient(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        checkLocationPermissions(context)
        // Usar Armenia, Quindío por defecto siempre
        setDefaultLocation()
    }
    
    private fun checkLocationPermissions(context: Context) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        _locationState.value = _locationState.value.copy(hasPermission = hasPermission)
        
        // No obtener ubicación automáticamente, usar Armenia por defecto
        Log.d("LocationViewModel", "Permisos de ubicación: $hasPermission")
    }
    
    private fun setDefaultLocation() {
        _locationState.value = _locationState.value.copy(
            latitude = 4.533889,  // Armenia, Quindío, Colombia
            longitude = -75.681111,
            isLoading = false,
            error = null
        )
        Log.d("LocationViewModel", "Ubicación establecida: Armenia, Quindío, Colombia (4.533889, -75.681111)")
    }
    
    fun onPermissionGranted() {
        _locationState.value = _locationState.value.copy(hasPermission = true)
        // Mantener Armenia como ubicación por defecto, no obtener ubicación real automáticamente
        Log.d("LocationViewModel", "Permisos otorgados, manteniendo Armenia como ubicación por defecto")
    }
    
    fun onPermissionDenied() {
        _locationState.value = _locationState.value.copy(
            hasPermission = false,
            error = null // No mostrar error, Armenia funciona sin permisos
        )
        Log.d("LocationViewModel", "Permisos denegados, usando Armenia como ubicación por defecto")
    }
    
    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        viewModelScope.launch {
            try {
                _locationState.value = _locationState.value.copy(isLoading = true, error = null)
                
                val location = getLastKnownLocation()
                if (location != null) {
                    _locationState.value = _locationState.value.copy(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        isLoading = false
                    )
                    Log.d("LocationViewModel", "Ubicación obtenida: ${location.latitude}, ${location.longitude}")
                } else {
                    // Si no hay ubicación guardada, usar ubicación por defecto (Armenia, Quindío, Colombia)
                    _locationState.value = _locationState.value.copy(
                        latitude = 4.533889,
                        longitude = -75.681111,
                        isLoading = false,
                        error = "Usando ubicación por defecto: Armenia, Quindío"
                    )
                    Log.d("LocationViewModel", "Usando ubicación por defecto: Armenia, Quindío, Colombia (4.533889, -75.681111)")
                }
                
            } catch (e: Exception) {
                Log.e("LocationViewModel", "Error obteniendo ubicación: ${e.message}")
                _locationState.value = _locationState.value.copy(
                    latitude = 4.533889, // Armenia, Quindío, Colombia por defecto
                    longitude = -75.681111,
                    isLoading = false,
                    error = "Ubicación por defecto: Armenia, Quindío"
                )
            }
        }
    }
    
    @SuppressLint("MissingPermission")
    private suspend fun getLastKnownLocation(): Location? = suspendCancellableCoroutine { continuation ->
        fusedLocationClient?.let { client ->
            val cancellationTokenSource = CancellationTokenSource()
            
            // Intentar obtener ubicación actual primero
            client.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(location)
                } else {
                    // Si no se puede obtener ubicación actual, intentar la última conocida
                    client.lastLocation.addOnSuccessListener { lastLocation ->
                        continuation.resume(lastLocation)
                    }.addOnFailureListener { exception ->
                        Log.e("LocationViewModel", "Error obteniendo última ubicación: ${exception.message}")
                        continuation.resume(null)
                    }
                }
            }.addOnFailureListener { exception ->
                Log.e("LocationViewModel", "Error obteniendo ubicación actual: ${exception.message}")
                continuation.resume(null)
            }
            
            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        } ?: continuation.resume(null)
    }
    
    // Función para obtener ubicación real del dispositivo (solo cuando se solicite)
    fun getCurrentLocationExplicitly() {
        if (_locationState.value.hasPermission) {
            getCurrentLocation()
        } else {
            Log.d("LocationViewModel", "Sin permisos para obtener ubicación real, manteniendo Armenia")
        }
    }
    
    fun clearError() {
        _locationState.value = _locationState.value.copy(error = null)
    }
    
    fun setLocation(latitude: Double, longitude: Double) {
        _locationState.value = _locationState.value.copy(
            latitude = latitude,
            longitude = longitude,
            isLoading = false,
            error = null
        )
        Log.d("LocationViewModel", "Ubicación establecida manualmente: $latitude, $longitude")
    }
}