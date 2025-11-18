package co.edu.eam.unilocal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import co.edu.eam.unilocal.models.Place
import android.util.Log
import com.google.android.gms.maps.model.BitmapDescriptorFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceMapScreen(
    modifier: Modifier = Modifier,
    place: Place?,
    onBackClick: () -> Unit = {}
) {
    if (place == null) {
        // Si no hay lugar, mostrar mensaje y volver
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No se encontró el lugar")
        }
        return
    }
    
    // Verificar si las coordenadas son válidas
    val hasValidCoordinates = place.latitude != 0.0 && place.longitude != 0.0
    
    // Ubicación del lugar (usar Armenia por defecto si las coordenadas son 0,0)
    val defaultLocation = LatLng(4.533889, -75.681111) // Armenia, Quindío
    val placeLocation = if (hasValidCoordinates) {
        LatLng(place.latitude, place.longitude)
    } else {
        defaultLocation
    }
    
    Log.d("PlaceMapScreen", "Mostrando mapa para: ${place.name}")
    Log.d("PlaceMapScreen", "Coordenadas: ${place.latitude}, ${place.longitude}")
    Log.d("PlaceMapScreen", "Coordenadas válidas: $hasValidCoordinates")
    
    // Configurar posición inicial de la cámara
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(placeLocation, 16f)
    }
    
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            text = place.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                        Text(
                            text = place.address,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Mapa
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapLoaded = {
                    Log.d("PlaceMapScreen", "Mapa cargado exitosamente")
                },
                properties = MapProperties(
                    isMyLocationEnabled = false,
                    mapType = MapType.NORMAL,
                    isTrafficEnabled = false,
                    isIndoorEnabled = true,
                    isBuildingEnabled = true,
                    mapStyleOptions = null
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    zoomGesturesEnabled = true,
                    scrollGesturesEnabled = true,
                    tiltGesturesEnabled = true,
                    rotationGesturesEnabled = true,
                    myLocationButtonEnabled = false,
                    mapToolbarEnabled = true,
                    compassEnabled = true,
                    scrollGesturesEnabledDuringRotateOrZoom = true
                )
            ) {
                // Marcador del lugar
                Marker(
                    state = MarkerState(position = placeLocation),
                    title = place.name,
                    snippet = "${place.category} • ${place.address}",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                )
            }
            
            // Tarjeta de información en la parte inferior
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = place.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = place.category,
                        fontSize = 14.sp,
                        color = Color(0xFF6200EE),
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = place.address,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    if (!hasValidCoordinates) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFF3E0)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⚠️",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = "Este lugar no tiene ubicación guardada. Mostrando ubicación por defecto.",
                                    fontSize = 12.sp,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    } else {
                        Text(
                            text = "Lat: ${String.format("%.6f", place.latitude)}, " +
                                   "Lng: ${String.format("%.6f", place.longitude)}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    
                    if (place.phone.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "📞 ${place.phone}",
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}
