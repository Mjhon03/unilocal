package co.edu.eam.unilocal.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import co.edu.eam.unilocal.R
import co.edu.eam.unilocal.ui.theme.MyApplicationTheme
import co.edu.eam.unilocal.models.Place
import co.edu.eam.unilocal.models.Review
import co.edu.eam.unilocal.ui.components.WriteReviewDialog
import co.edu.eam.unilocal.viewmodels.AuthViewModel
import co.edu.eam.unilocal.viewmodels.ReviewViewModel
import co.edu.eam.unilocal.viewmodels.SharedPlaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onCallClick: () -> Unit = {},
    onMapClick: () -> Unit = {},
    sharedPlaceViewModel: SharedPlaceViewModel = viewModel(),
    reviewViewModel: ReviewViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    var isFavorite by remember { mutableStateOf(false) }
    var showWriteReviewDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    val selectedPlace by sharedPlaceViewModel.selectedPlace.collectAsState()
    val isLoading by sharedPlaceViewModel.isLoading.collectAsState()
    val errorMessage by sharedPlaceViewModel.errorMessage.collectAsState()
    
    // Review states
    val reviews by reviewViewModel.reviews.collectAsState()
    val reviewsLoading by reviewViewModel.isLoading.collectAsState()
    val averageRating by reviewViewModel.averageRating.collectAsState()
    val hasUserReviewed by reviewViewModel.hasUserReviewed.collectAsState()
    val reviewError by reviewViewModel.errorMessage.collectAsState()
    
    // Auth state
    val authState by authViewModel.authState.collectAsState()

    // Cargar reseñas cuando se selecciona un lugar
    LaunchedEffect(selectedPlace, authState) {
        selectedPlace?.let { place ->
            android.util.Log.d("PlaceDetailScreen", "Lugar seleccionado: ${place.name} (${place.id})")
            reviewViewModel.loadReviewsForPlace(place.id)
            val currentAuthState = authState
            if (currentAuthState is co.edu.eam.unilocal.viewmodels.AuthState.Authenticated) {
                reviewViewModel.checkUserReviewed(currentAuthState.user.id, place.id)
            }
        }
    }
    
    // Log para debug de cambios en reviews
    LaunchedEffect(reviews.size) {
        android.util.Log.d("PlaceDetailScreen", "Número de reseñas actualizado: ${reviews.size}")
    }
    
    // Mostrar estado de carga
    if (isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = Color.Black,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Cargando detalles del lugar...",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
        return
    }
    
    // Mostrar error si existe
    if (errorMessage != null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = errorMessage ?: "Error desconocido",
                fontSize = 16.sp,
                color = Color.Red,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBackClick) {
                Text("Volver")
            }
        }
        return
    }
    
    // Si no hay lugar seleccionado, mostrar mensaje
    if (selectedPlace == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No se ha seleccionado ningún lugar",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBackClick) {
                Text("Volver")
            }
        }
        return
    }
    
    val place = selectedPlace!!
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onShareClick) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartir",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { isFavorite = !isFavorite; onFavoriteClick() }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "Eliminar de favoritos" else "Agregar a favoritos",
                            tint = if (isFavorite) Color.Red else Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            // Solo mostrar el FAB si el usuario está autenticado y no ha hecho una reseña
            val currentAuthState = authState
            if (currentAuthState is co.edu.eam.unilocal.viewmodels.AuthState.Authenticated && !hasUserReviewed) {
                FloatingActionButton(
                    onClick = { showWriteReviewDialog = true },
                    containerColor = Color(0xFF6200EE),
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Escribir reseña"
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagen principal del lugar
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    // Aquí podrías agregar una imagen del lugar si está disponible
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Lugar",
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                    }
                }
            }

            // Información principal del lugar
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = place.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Rating promedio
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = if (index < averageRating.toInt()) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "Estrella ${index + 1}",
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = String.format("%.1f", averageRating),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                            Text(
                                text = " (${reviews.size} reseñas)",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = place.category,
                            fontSize = 14.sp,
                            color = Color(0xFF6200EE),
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Ubicación",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = place.address,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                        
                        if (place.phone.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Teléfono",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = place.phone,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // Descripción
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Descripción",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = place.description.ifEmpty { "No hay descripción disponible." },
                            fontSize = 14.sp,
                            color = Color.Gray,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // Botones de acción
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (place.phone.isNotEmpty()) {
                        OutlinedButton(
                            onClick = onCallClick,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Llamar",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Llamar")
                        }
                    }
                    
                    Button(
                        onClick = onMapClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6200EE)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Ver en mapa",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mapa")
                    }
                }
            }

            // Sección de reseñas
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Reseñas",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            
                            if (reviewsLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color(0xFF6200EE)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (reviews.isEmpty() && !reviewsLoading) {
                            Text(
                                text = "No hay reseñas aún. ¡Sé el primero en escribir una!",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Lista de reseñas
            items(reviews) { review ->
                ReviewItem(
                    review = review,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            
            // Espaciado final para el FAB
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Dialog para escribir reseña
    if (showWriteReviewDialog) {
        WriteReviewDialog(
            placeName = place.name,
            onDismiss = { showWriteReviewDialog = false },
            onSubmit = { rating, comment ->
                val currentAuthState = authState
                if (currentAuthState is co.edu.eam.unilocal.viewmodels.AuthState.Authenticated) {
                    val user = currentAuthState.user
                    reviewViewModel.createReview(
                        placeId = place.id,
                        userId = user.id,
                        userName = user.username,
                        rating = rating,
                        comment = comment,
                        onComplete = { success, message ->
                            android.util.Log.d("PlaceDetailScreen", "Review creation result: $success, message: $message")
                            if (success) {
                                showWriteReviewDialog = false
                                // Forzar recarga de reseñas después de un pequeño delay
                                // para asegurar que Firebase ha procesado la escritura
                                coroutineScope.launch {
                                    kotlinx.coroutines.delay(500)
                                    val userId = if (currentAuthState is co.edu.eam.unilocal.viewmodels.AuthState.Authenticated) {
                                        currentAuthState.user.id
                                    } else null
                                    reviewViewModel.refreshPlaceData(place.id, userId)
                                }
                            }
                        }
                    )
                }
            },
            isLoading = reviewsLoading
        )
    }
}

@Composable
fun ReviewItem(
    review: Review,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar del usuario
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF6200EE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = review.userInitials,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = review.userName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        
                        // Rating con estrellas
                        Row {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = if (index < review.rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "Estrella ${index + 1}",
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
                
                Text(
                    text = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                        .format(java.util.Date(review.createdAt)),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            if (review.comment.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = review.comment,
                    fontSize = 14.sp,
                    color = Color.Black,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlaceDetailScreenPreview() {
    MyApplicationTheme {
        PlaceDetailScreen()
    }
}