package co.edu.eam.unilocal.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import co.edu.eam.unilocal.models.Place
import co.edu.eam.unilocal.viewmodels.LocationState

@Composable
fun MapBoxMap(
    modifier: Modifier = Modifier,
    locationState: LocationState,
    places: List<Place> = emptyList(),
    onMapReady: () -> Unit = {},
    onPlaceClick: (Place) -> Unit = {}
) {
    val context = LocalContext.current
    
    
    
    
    // Ubicación inicial (Armenia, Quindío, Colombia por defecto)
    val defaultLocation = LatLng(4.533889, -75.681111)
    val currentLocation = if (locationState.latitude != 0.0 && locationState.longitude != 0.0) {
        LatLng(locationState.latitude, locationState.longitude)
    } else {
        defaultLocation
    }
    
    // Configurar posición inicial de la cámara
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, 14f)
    }
    
    // Actualizar posición de la cámara cuando cambie la ubicación
    LaunchedEffect(locationState.latitude, locationState.longitude) {
        if (locationState.latitude != 0.0 && locationState.longitude != 0.0) {
            val newLocation = LatLng(locationState.latitude, locationState.longitude)
            cameraPositionState.animate(
                update = com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(newLocation, 14f),
                durationMs = 1000
            )
        }
    }
    
    // Estado para controlar si el mapa se carga correctamente
    var mapLoaded by remember { mutableStateOf(false) }
    var mapLoadTimeout by remember { mutableStateOf(false) }
    
    // Timeout para detectar si el mapa no se carga en 8 segundos
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(8000) // 8 segundos
        if (!mapLoaded) {
            android.util.Log.w("MapBoxMap", "Timeout: El mapa no se cargó en 8 segundos - verificar API key")
            mapLoadTimeout = true
        }
    }
    
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        onMapLoaded = {
            mapLoaded = true
            onMapReady()
        },
        properties = MapProperties(
            isMyLocationEnabled = locationState.hasPermission,
            mapType = MapType.NORMAL
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            zoomGesturesEnabled = true,
            scrollGesturesEnabled = true,
            tiltGesturesEnabled = true,
            rotationGesturesEnabled = true,
            myLocationButtonEnabled = false, // Deshabilitado para usar Armenia por defecto
            mapToolbarEnabled = false
        )
    ) {
        // Marcadores de lugares
        places.forEach { place ->
            if (place.latitude != 0.0 && place.longitude != 0.0) {
                Marker(
                    state = MarkerState(position = LatLng(place.latitude, place.longitude)),
                    title = place.name,
                    snippet = "${place.category} • ${place.address}",
                    onInfoWindowClick = {
                        onPlaceClick(place)
                    }
                )
            }
        }
        
        // Marcador de ubicación (Armenia, Quindío por defecto)
        if (locationState.latitude != 0.0 && locationState.longitude != 0.0) {
            Marker(
                state = MarkerState(position = currentLocation),
                title = "Armenia, Quindío",
                snippet = if (places.isEmpty()) 
                    "Centro de Armenia • No hay lugares cercanos"
                else 
                    "Centro de Armenia • ${places.size} lugares cercanos",
                icon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(
                    com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN
                )
            )
        }
    }
    
    // Mostrar mensaje de error si existe
    if (locationState.error != null && !locationState.hasPermission) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ubicación requerida",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = locationState.error,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
    
    // Mostrar mensaje de error si el mapa no se puede cargar
    if (mapLoadTimeout && !mapLoaded) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Red
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "🗺️ Mapa no disponible",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Se requiere configurar una API key válida de Google Maps.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pasos para solucionarlo:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "1. Ve a Google Cloud Console\n2. Habilita Maps SDK for Android\n3. Crea una API key\n4. Actualiza values/maps.xml",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 16.sp
                    )
                    
                    if (locationState.latitude != 0.0 && locationState.longitude != 0.0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        androidx.compose.material3.HorizontalDivider(
                            color = Color.Gray.copy(alpha = 0.3f),
                            thickness = 1.dp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "📍 Tu ubicación actual:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Lat: ${String.format("%.6f", locationState.latitude)}\nLng: ${String.format("%.6f", locationState.longitude)}",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }
                    
                    if (places.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "🏢 ${places.size} lugares encontrados cerca",
                            fontSize = 12.sp,
                            color = Color(0xFF6200EE),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
    
    // Mostrar indicador de carga
    if (locationState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Color.Black
            )
        }
    }
}

