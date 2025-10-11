package co.edu.eam.unilocal.ui.screens

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.eam.unilocal.ui.theme.MyApplicationTheme
import co.edu.eam.unilocal.R
import co.edu.eam.unilocal.viewmodels.PlacesViewModel
import co.edu.eam.unilocal.viewmodels.AuthViewModel
import co.edu.eam.unilocal.models.Place
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import co.edu.eam.unilocal.viewmodels.AuthState
import co.edu.eam.unilocal.services.PlaceService
import co.edu.eam.unilocal.models.ModerationPlace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaceScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onCreateClick: () -> Unit = {},
    onAddPhotoClick: () -> Unit = {},
    onMapClick: () -> Unit = {},
    placesViewModel: PlacesViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    var placeName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var openingTime by remember { mutableStateOf("") }
    var closingTime by remember { mutableStateOf("") }
    
    var selectedDays by remember { mutableStateOf(setOf<String>()) }
    val daysOfWeek = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
    
    val currentUser by authViewModel.currentUser.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val placeService = PlaceService()
    
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
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
                    .padding(16.dp),
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
                    fontSize = 18.sp,
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
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
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAddPhotoClick() },
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
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.add_icon_desc),
                                modifier = Modifier.size(48.dp),
                                tint = Color.Gray
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = stringResource(R.string.add_photo),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                            
                            Text(
                                text = stringResource(R.string.add_photo_description),
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
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
                    
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        placeholder = { Text(stringResource(R.string.category_placeholder)) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = stringResource(R.string.dropdown_icon_desc),
                                tint = Color.Gray
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Chips de categorías sugeridas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Restaurantes", "Cafetería", "Hoteles", "Museos").forEach { cat ->
                            FilterChip(
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 12.sp) },
                                selected = category == cat,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF6200EE),
                                    selectedLabelColor = Color.White
                                )
                            )
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
                    
                    // Mapa simulado
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clickable { onMapClick() },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = stringResource(R.string.location_icon_desc),
                                        tint = Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = stringResource(R.string.locate_on_map),
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
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
                        if (user != null && validateForm()) {
                            val newPlace = Place(
                                name = placeName.trim(),
                                category = category.trim(),
                                description = description.trim(),
                                address = address.trim(),
                                phone = phone.trim(),
                                openingTime = openingTime.trim(),
                                closingTime = closingTime.trim(),
                                workingDays = selectedDays.toList(),
                                createdBy = user.id,
                                latitude = 4.5389 + (Math.random() * 0.005),
                                longitude = -75.6681 + (Math.random() * 0.005),
                                rating = 0.0,
                                isApproved = false
                            )

                            // Add locally to the ViewModel immediately for optimistic UI
                            placesViewModel.addPlace(newPlace)

                            // Build moderation payload and send to Firestore
                            val moderationPlace = ModerationPlace(
                                id = "",
                                name = newPlace.name,
                                description = newPlace.description,
                                address = newPlace.address,
                                submittedBy = user.id,
                                phone = if (newPlace.phone.isBlank()) null else newPlace.phone,
                                website = null,
                                imageUrl = "",
                                createdAt = System.currentTimeMillis().toString()
                            )

                            coroutineScope.launch {
                                val result = placeService.createModerationPlace(moderationPlace)
                                if (result.isSuccess) {
                                    snackbarHostState.showSnackbar("Lugar enviado para moderación")
                                    // Navigate back / complete creation
                                    onCreateClick()
                                } else {
                                    val msg = result.exceptionOrNull()?.message ?: "Error al enviar"
                                    snackbarHostState.showSnackbar("Error: $msg")
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
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.create_place_button),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
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
