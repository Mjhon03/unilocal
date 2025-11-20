package co.edu.eam.unilocal.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.eam.unilocal.ui.theme.MyApplicationTheme
import co.edu.eam.unilocal.R
import co.edu.eam.unilocal.viewmodels.PlacesViewModel
import co.edu.eam.unilocal.viewmodels.AuthViewModel
import co.edu.eam.unilocal.models.Place
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import co.edu.eam.unilocal.viewmodels.AuthState
import co.edu.eam.unilocal.services.PlaceService
import co.edu.eam.unilocal.services.ImageUploadService
import co.edu.eam.unilocal.models.ModerationPlace
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaceScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onCreateClick: () -> Unit = {},
    onAddPhotoClick: () -> Unit = {},
    onMapClick: () -> Unit = {},
    placesViewModel: PlacesViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    locationViewModel: co.edu.eam.unilocal.viewmodels.LocationViewModel = viewModel()
) {
    var placeName by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var expandedCategoryMenu by remember { mutableStateOf(false) }
    val categories = listOf(
        "Restaurante",
        "Cafetería",
        "Hotel",
        "Museo",
        "Parque",
        "Gimnasio",
        "Cine",
        "Teatro",
        "Bar",
        "Discoteca",
        "Biblioteca",
        "Hospital",
        "Supermercado",
        "Centro Comercial",
        "Otro"
    )
    var description by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var openingTime by rememberSaveable { mutableStateOf("") }
    var closingTime by rememberSaveable { mutableStateOf("") }
    
    var selectedDays by rememberSaveable { mutableStateOf(setOf<String>()) }
    val daysOfWeek = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
    
    var placePhotoUrls by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    var isUploadingPhoto by remember { mutableStateOf(false) }
    var photoError by remember { mutableStateOf<String?>(null) }
    
    val currentUser by authViewModel.currentUser.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val locationState by locationViewModel.locationState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val placeService = PlaceService()
    val context = LocalContext.current
    val imageUploadService = ImageUploadService(context)
    
    // Inicializar cliente de ubicación solo una vez
    LaunchedEffect(locationViewModel) {
        locationViewModel.initializeLocationClient(context)
        Log.d("CreatePlaceScreen", "LocationViewModel inicializado, estado: ${locationState.latitude}, ${locationState.longitude}")
    }
    
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isCreatingPlace by remember { mutableStateOf(false) }
    
    // File launcher para seleccionar fotos
    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            Log.d("CreatePlaceScreen", "Photo selected: $uri")
            isUploadingPhoto = true
            photoError = null
            
            scope.launch {
                try {
                    Log.d("CreatePlaceScreen", "Starting upload...")
                    val imageUrl = imageUploadService.uploadImage(uri)
                    Log.d("CreatePlaceScreen", "Upload success: $imageUrl")
                    
                    // Update UI
                    placePhotoUrls = placePhotoUrls + imageUrl
                    isUploadingPhoto = false
                    Toast.makeText(context, "Foto añadida", Toast.LENGTH_SHORT).show()
                    
                } catch (e: Exception) {
                    Log.e("CreatePlaceScreen", "Upload failed: ${e.message}", e)
                    photoError = e.message ?: "Error desconocido"
                    isUploadingPhoto = false
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
        if(it){
            Toast.makeText(context, "Permiso concedido", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }
    
    fun validateForm(): Boolean {
        return when {
            placeName.isBlank() -> {
                errorMessage = "El nombre del lugar es obligatorio"
                showError = true
                false
            }
            category.isBlank() -> {
                errorMessage = "La categoría es obligatoria"
                showError = true
                false
            }
            description.isBlank() -> {
                errorMessage = "La descripción es obligatoria"
                showError = true
                false
            }
            address.isBlank() -> {
                errorMessage = "La dirección es obligatoria"
                showError = true
                false
            }
            selectedDays.isEmpty() -> {
                errorMessage = "Selecciona al menos un día de funcionamiento"
                showError = true
                false
            }
            openingTime.isBlank() || closingTime.isBlank() -> {
                errorMessage = "Los horarios son obligatorios"
                showError = true
                false
            }
            placePhotoUrls.isEmpty() -> {
                errorMessage = "Debe agregar al menos una foto del lugar"
                showError = true
                false
            }
            !locationState.isManuallySet -> {
                errorMessage = "Debe seleccionar una ubicación en el mapa"
                showError = true
                false
            }
            locationState.latitude == 0.0 && locationState.longitude == 0.0 -> {
                errorMessage = "La ubicación no es válida"
                showError = true
                false
            }
            else -> {
                showError = false
                true
            }
        }
    }
    
    // Mostrar el mensaje de error si hay alguno
    LaunchedEffect(showError) {
        if (showError) {
            snackbarHostState.showSnackbar(errorMessage)
        }
    }
    
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_icon_desc),
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.create_place),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Sección Fotos del lugar
            item {
                Column {
                    Text(
                        text = stringResource(R.string.place_photos),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Mostrar fotos cargadas
                    if (placePhotoUrls.isNotEmpty()) {
                        placePhotoUrls.forEachIndexed { index, photoUrl ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .padding(bottom = 8.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                AsyncImage(
                                    model = photoUrl,
                                    contentDescription = "Foto del lugar ${index + 1}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    
                    // Botón para agregar más fotos
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val permissionCheckResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES)
                                } else {
                                    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                                }

                                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                                    photoLauncher.launch("image/*")
                                } else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                    }
                                }
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (isUploadingPhoto) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(48.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = stringResource(R.string.add_icon_desc),
                                    modifier = Modifier.size(48.dp),
                                    tint = Color.Gray
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = stringResource(R.string.add_photo),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                            
                            Text(
                                text = if (isUploadingPhoto) "Subiendo..." else stringResource(R.string.add_photo_description),
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    
                    if (photoError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Error: $photoError",
                            fontSize = 12.sp,
                            color = Color.Red
                        )
                    }
                }
            }
            
            // Campo Nombre del lugar
            item {
                Column {
                    Text(
                        text = stringResource(R.string.place_name),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = placeName,
                        onValueChange = { placeName = it },
                        placeholder = { Text(stringResource(R.string.place_name_placeholder)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
            
            // Campo Categoría
            item {
                Column {
                    Text(
                        text = stringResource(R.string.category),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedCategoryMenu = true }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { },
                            readOnly = true,
                            placeholder = { Text(stringResource(R.string.category_placeholder)) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = stringResource(R.string.dropdown_icon_desc),
                                    tint = Color.Gray
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            enabled = false,
                            colors = androidx.compose.material3.TextFieldDefaults.colors(
                                disabledTextColor = Color.Black,
                                disabledContainerColor = Color.Transparent,
                                disabledPlaceholderColor = Color.Gray,
                                disabledTrailingIconColor = Color.Gray
                            )
                        )
                        
                        DropdownMenu(
                            expanded = expandedCategoryMenu,
                            onDismissRequest = { expandedCategoryMenu = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        category = cat
                                        expandedCategoryMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            // Campo Descripción
            item {
                Column {
                    Text(
                        text = stringResource(R.string.description),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text(stringResource(R.string.description_placeholder)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 4
                    )
                }
            }
            
            // Sección Ubicación
            item {
                Column {
                    Text(
                        text = stringResource(R.string.location),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Campo Dirección
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        placeholder = { Text(stringResource(R.string.address_placeholder)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Botón para seleccionar ubicación en el mapa
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMapClick() },
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                locationState.isManuallySet -> Color(0xFFE8F5E9) // Verde claro
                                locationState.latitude != 0.0 && locationState.longitude != 0.0 -> Color(0xFFFFF3E0) // Naranja claro
                                else -> Color(0xFFF5F5F5) // Gris claro
                            }
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = stringResource(R.string.location_icon_desc),
                                    tint = when {
                                        locationState.isManuallySet -> Color(0xFF4CAF50)
                                        locationState.latitude != 0.0 && locationState.longitude != 0.0 -> Color(0xFFFF9800)
                                        else -> Color.Gray
                                    },
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when {
                                        locationState.isManuallySet -> "Ubicación confirmada"
                                        locationState.latitude != 0.0 && locationState.longitude != 0.0 -> "Ubicación actual"
                                        else -> stringResource(R.string.locate_on_map)
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = when {
                                        locationState.isManuallySet -> Color(0xFF4CAF50)
                                        locationState.latitude != 0.0 && locationState.longitude != 0.0 -> Color(0xFFFF9800)
                                        else -> Color.Gray
                                    }
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            if (locationState.latitude != 0.0 && locationState.longitude != 0.0) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Lat: ${String.format("%.4f", locationState.latitude)}, " +
                                               "Lng: ${String.format("%.4f", locationState.longitude)}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                    if (locationState.isManuallySet) {
                                        Text(
                                            text = "✓ Ubicación confirmada",
                                            fontSize = 11.sp,
                                            color = Color(0xFF4CAF50),
                                            fontWeight = FontWeight.Medium
                                        )
                                    } else {
                                        Text(
                                            text = "Ubicación del dispositivo - Tap para cambiar",
                                            fontSize = 11.sp,
                                            color = Color(0xFFFF9800),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = stringResource(R.string.tap_to_open_map),
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
            
            // Sección Información de contacto
            item {
                Column {
                    Text(
                        text = stringResource(R.string.contact_info),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        placeholder = { Text(stringResource(R.string.phone_placeholder)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
            
            // Sección Horarios
            item {
                Column {
                    Text(
                        text = stringResource(R.string.schedule),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Días de funcionamiento
                    Text(
                        text = stringResource(R.string.working_days),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        daysOfWeek.forEach { day ->
                            FilterChip(
                                onClick = {
                                    selectedDays = if (selectedDays.contains(day)) {
                                        selectedDays - day
                                    } else {
                                        selectedDays + day
                                    }
                                },
                                label = { Text(day) },
                                selected = selectedDays.contains(day),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF6200EE),
                                    selectedLabelColor = Color.White,
                                    containerColor = Color(0xFFF5F5F5),
                                    labelColor = Color.Black
                                )
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Horas de apertura y cierre
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Hora de apertura
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.opening_time),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = openingTime,
                                onValueChange = { openingTime = it },
                                placeholder = { Text(stringResource(R.string.time_placeholder)) },
                                trailingIcon = {
                                    Text("🕒", fontSize = 16.sp)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        
                        // Hora de cierre
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.closing_time),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = closingTime,
                                onValueChange = { closingTime = it },
                                placeholder = { Text(stringResource(R.string.time_placeholder)) },
                                trailingIcon = {
                                    Text("🕒", fontSize = 16.sp)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Botón Crear lugar
            item {
                Button(
                    onClick = {
                        val user = currentUser
                        if (user != null && validateForm() && !isCreatingPlace) {
                            isCreatingPlace = true
                            Log.d("CreatePlace", "Creando lugar con coordenadas: ${locationState.latitude}, ${locationState.longitude}")
                            
                            val newPlace = Place(
                                name = placeName.trim(),
                                category = category.trim(),
                                description = description.trim(),
                                address = address.trim(),
                                phone = phone.trim(),
                                openingTime = openingTime.trim(),
                                closingTime = closingTime.trim(),
                                workingDays = selectedDays.toList(),
                                photoUrls = placePhotoUrls,
                                createdBy = user.id,
                                latitude = locationState.latitude,
                                longitude = locationState.longitude,
                                rating = 0.0,
                                isApproved = false
                            )
                            
                            Log.d("CreatePlace", "Lugar creado - Nombre: ${newPlace.name}, Lat: ${newPlace.latitude}, Lng: ${newPlace.longitude}")

                            // Add locally to the ViewModel immediately for optimistic UI
                            placesViewModel.addPlace(newPlace)

                            // Build moderation payload and send to Firestore
                            val moderationPlace = ModerationPlace(
                                id = "",
                                name = newPlace.name,
                                category = newPlace.category,
                                description = newPlace.description,
                                address = newPlace.address,
                                submittedBy = user.id,
                                openingTime = newPlace.openingTime,
                                closingTime = newPlace.closingTime,
                                workingDays = newPlace.workingDays,
                                phone = if (newPlace.phone.isBlank()) null else newPlace.phone,
                                website = null,
                                photoUrls = placePhotoUrls,
                                latitude = newPlace.latitude,
                                longitude = newPlace.longitude,
                                createdAt = System.currentTimeMillis().toString()
                            )
                            
                            Log.d("CreatePlace", "ModerationPlace con coordenadas: ${moderationPlace.latitude}, ${moderationPlace.longitude}")

                            scope.launch {
                                try {
                                    // Mostrar mensaje de que está procesando
                                    Toast.makeText(context, "Creando lugar...", Toast.LENGTH_SHORT).show()
                                    
                                    val result = placeService.createModerationPlace(moderationPlace)
                                    if (result.isSuccess) {
                                        // Mostrar mensaje de éxito con Toast
                                        Toast.makeText(
                                            context,
                                            "¡Lugar creado exitosamente!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        
                                        Log.d("CreatePlace", "Lugar creado exitosamente, navegando de vuelta")
                                        
                                        // Navegar de vuelta al inicio sin delay para evitar problemas
                                        onCreateClick()
                                    } else {
                                        val msg = result.exceptionOrNull()?.message ?: "Error al enviar"
                                        Log.e("CreatePlace", "Error al crear lugar: $msg")
                                        Toast.makeText(context, "Error: $msg", Toast.LENGTH_LONG).show()
                                        snackbarHostState.showSnackbar(
                                            message = "Error: $msg",
                                            duration = androidx.compose.material3.SnackbarDuration.Long
                                        )
                                        isCreatingPlace = false
                                    }
                                } catch (e: Exception) {
                                    Log.e("CreatePlace", "Excepción al crear lugar", e)
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                    isCreatingPlace = false
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isCreatingPlace
                ) {
                    if (isCreatingPlace) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Creando lugar...",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.create_place_button),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            // Mensaje final
            item {
                Text(
                    text = stringResource(R.string.moderation_notice),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreatePlaceScreenPreview() {
    MyApplicationTheme {
        CreatePlaceScreen()
    }
}
