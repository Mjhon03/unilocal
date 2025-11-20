package co.edu.eam.unilocal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import co.edu.eam.unilocal.viewmodels.LocationViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.isSystemInDarkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onLocationSelected: (Double, Double, String) -> Unit = { _, _, _ -> },
    locationViewModel: LocationViewModel = viewModel()
) {
    // Forzar tema claro para evitar modo oscuro en el mapa
    CompositionLocalProvider(
        LocalContentColor provides Color.Black
    ) {
        LocationPickerContent(
            modifier = modifier,
            onBackClick = onBackClick,
            onLocationSelected = onLocationSelected,
            locationViewModel = locationViewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationPickerContent(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onLocationSelected: (Double, Double, String) -> Unit = { _, _, _ -> },
    locationViewModel: LocationViewModel = viewModel()
) {
    val context = LocalContext.current
    val locationState by locationViewModel.locationState.collectAsState()
    
    // Ubicación por defecto: Armenia, Quindío, Colombia
    val defaultLocation = LatLng(4.533889, -75.681111)
    
    // Estado del centro del mapa (donde está el pin)
    var selectedLocation by remember { 
        val lat = if (locationState.latitude != 0.0) locationState.latitude else defaultLocation.latitude
        val lng = if (locationState.longitude != 0.0) locationState.longitude else defaultLocation.longitude
        mutableStateOf(LatLng(lat, lng))
    }
    var address by remember { mutableStateOf("") }
    
    // Inicializar cliente de ubicación
    LaunchedEffect(Unit) {
        locationViewModel.initializeLocationClient(context)
        Log.d("LocationPicker", "Inicializando con ubicación: ${locationState.latitude}, ${locationState.longitude}")
    }
    
    // Actualizar ubicación seleccionada cuando cambia el estado de ubicación
    LaunchedEffect(locationState.latitude, locationState.longitude) {
        if (locationState.latitude != 0.0 && locationState.longitude != 0.0) {
            selectedLocation = LatLng(locationState.latitude, locationState.longitude)
            Log.d("LocationPicker", "Ubicación actualizada: ${selectedLocation.latitude}, ${selectedLocation.longitude}")
        }
    }
    
    // Configurar posición inicial de la cámara con Armenia, Quindío
    val cameraPositionState = rememberCameraPositionState {
        val initialLat = if (locationState.latitude != 0.0) locationState.latitude else defaultLocation.latitude
        val initialLng = if (locationState.longitude != 0.0) locationState.longitude else defaultLocation.longitude
        position = CameraPosition.fromLatLngZoom(LatLng(initialLat, initialLng), 15f)
    }
    
    // Actualizar ubicación seleccionada cuando se mueve la cámara
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            selectedLocation = cameraPositionState.position.target
            Log.d("LocationPicker", "Nueva ubicación: ${selectedLocation.latitude}, ${selectedLocation.longitude}")
        }
    }
    
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Seleccionar ubicación",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    ) 
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
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Botón para ir a mi ubicación
                FloatingActionButton(
                    onClick = {
                        locationViewModel.getCurrentLocationExplicitly()
                    },
                    containerColor = Color.White,
                    contentColor = Color(0xFF6200EE)
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Mi ubicación"
                    )
                }
                
                // Botón para confirmar ubicación
                FloatingActionButton(
                    onClick = {
                        // Guardar ubicación en el ViewModel
                        locationViewModel.setLocation(
                            selectedLocation.latitude,
                            selectedLocation.longitude
                        )
                        // Generar dirección aproximada
                        val generatedAddress = "Lat: ${String.format("%.4f", selectedLocation.latitude)}, " +
                                             "Lng: ${String.format("%.4f", selectedLocation.longitude)}"
                        onLocationSelected(
                            selectedLocation.latitude,
                            selectedLocation.longitude,
                            generatedAddress
                        )
                    },
                    containerColor = Color(0xFF6200EE)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Confirmar ubicación",
                        tint = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Mapa - forzar siempre tema claro
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapLoaded = {
                    Log.d("LocationPicker", "Mapa cargado exitosamente")
                    Log.d("LocationPicker", "Posición de cámara: ${cameraPositionState.position.target.latitude}, ${cameraPositionState.position.target.longitude}")
                },
                properties = MapProperties(
                    isMyLocationEnabled = locationState.hasPermission,
                    mapType = MapType.NORMAL,
                    isTrafficEnabled = false,
                    isIndoorEnabled = true,
                    isBuildingEnabled = true,
                    // Forzar tema claro del mapa independientemente del tema del sistema
                    mapStyleOptions = null // Esto fuerza el estilo por defecto (claro) de Google Maps
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    zoomGesturesEnabled = true,
                    scrollGesturesEnabled = true,
                    tiltGesturesEnabled = true,
                    rotationGesturesEnabled = true,
                    myLocationButtonEnabled = false,
                    mapToolbarEnabled = false,
                    compassEnabled = true,
                    scrollGesturesEnabledDuringRotateOrZoom = true
                )
            )
            
            // Pin central fijo
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Pin de ubicación",
                        modifier = Modifier.size(48.dp),
                        tint = Color.Red
                    )
                    // Sombra del pin
                    Box(
                        modifier = Modifier
                            .size(16.dp, 4.dp)
                            .offset(y = (-4).dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.3f),
                                shape = CircleShape
                            )
                    )
                }
            }
            
            // Tarjeta de información en la parte superior
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Mueve el mapa para seleccionar la ubicación",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Lat: ${String.format("%.6f", selectedLocation.latitude)}, " +
                               "Lng: ${String.format("%.6f", selectedLocation.longitude)}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            // Mostrar cargando si está obteniendo ubicación
            if (locationState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Obteniendo ubicación...")
                        }
                    }
                }
            }
        }
    }
}
