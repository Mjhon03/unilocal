package co.edu.eam.unilocal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.eam.unilocal.ui.theme.MyApplicationTheme
import co.edu.eam.unilocal.R
import co.edu.eam.unilocal.viewmodels.PlacesViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import co.edu.eam.unilocal.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onCrearClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onBackClick : () -> Unit = {},
    onSeeAllClick: () -> Unit = {},
    onAdminClick: () -> Unit = {},
    onRequireAuth: () -> Unit = {},
    placesViewModel: PlacesViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todos") }
    var isSearchActive by remember { mutableStateOf(false) }
    
    val categories = listOf(
        "Todos", "Restaurantes", "Cafetería", "Hoteles", "Museos"
    )
    
    val searchResults by placesViewModel.searchResults.collectAsState()
    val favorites by placesViewModel.favorites.collectAsState()
    val isLoading by placesViewModel.isLoading.collectAsState()
    val errorMessage by placesViewModel.errorMessage.collectAsState()
    
    val currentUser by authViewModel.currentUser.collectAsState()
    
    // Log para depuración
    LaunchedEffect(searchResults) {
        android.util.Log.d("SearchScreen", "searchResults actualizados: ${searchResults.size} lugares")
        searchResults.forEach { place ->
            android.util.Log.d("SearchScreen", "  - ${place.name} (${place.category})")
        }
    }
    
    LaunchedEffect(isLoading) {
        android.util.Log.d("SearchScreen", "isLoading: $isLoading")
    }
    
    LaunchedEffect(errorMessage) {
        android.util.Log.d("SearchScreen", "errorMessage: $errorMessage")
    }
    
    // Sincronizar usuario actual con PlacesViewModel
    LaunchedEffect(currentUser) {
        placesViewModel.setCurrentUser(currentUser?.id)
    }
    
    // Buscar lugares cuando cambie la búsqueda o categoría
    LaunchedEffect(searchQuery, selectedCategory) {
        android.util.Log.d("SearchScreen", "Ejecutando búsqueda: query='$searchQuery', category='$selectedCategory'")
        placesViewModel.searchPlaces(searchQuery, selectedCategory)
    }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                // SearchBar de Material UI
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { isSearchActive = false },
                    active = isSearchActive,
                    onActiveChange = { isSearchActive = it },
                    placeholder = { Text(stringResource(R.string.search_placeholder)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.search_icon_desc)
                        )
                    },
                    trailingIcon = {
                        Row {
                            IconButton(
                                onClick = { /* Acción del filtro */ }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = stringResource(R.string.filters_icon_desc)
                                )
                            }

                            // Mostrar botón admin si el usuario es admin o moderator
                            if (authViewModel.isUserAdmin() || authViewModel.isUserModerator()) {
                                IconButton(
                                    onClick = { onAdminClick() }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = "Admin",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Contenido cuando la búsqueda está activa - mostrar resultados de búsqueda
                    LazyColumn {
                        items(searchResults.size) { index ->
                            val place = searchResults[index]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { /* Navegar a detalle */ }
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = place.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = "${place.category} • ${place.address}",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                                
                                IconButton(
                                    onClick = {
                                        if (currentUser == null) {
                                            onRequireAuth()
                                        } else {
                                            placesViewModel.toggleFavorite(place.id) { ok, msg ->
                                                android.util.Log.d("SearchScreen", "toggleFavorite result: ok=$ok msg=$msg")
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (favorites.contains(place.id)) 
                                            Icons.Filled.Favorite 
                                        else 
                                            Icons.Outlined.FavoriteBorder,
                                        contentDescription = "Favorito",
                                        tint = if (favorites.contains(place.id)) Color.Red else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Chips de categorías
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        FilterChip(
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            selected = selectedCategory == category,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.Black,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF5F5F5),
                                labelColor = Color.Black
                            )
                        )
                    }
                }
            }

        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                contentColor = Color.Black
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Mapa (activo)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .clickable { onBackClick() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = stringResource(R.string.home),
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.home),
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                    
                    // Buscar
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .clickable { isSearchActive = true }

                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = stringResource(R.string.search_icon_desc),
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.search),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    
                    // Crear
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .clickable { onCrearClick() }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = stringResource(R.string.create_icon_desc),
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.create),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    
                    // Favoritos
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .clickable { onFavoritesClick() }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Favorite,
                            contentDescription = stringResource(R.string.favorite_icon_desc),
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.favorites),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    
                    // Perfil
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .clickable { onProfileClick() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = stringResource(R.string.person_icon_desc),
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.profile),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5F5F5))
        ) {
            // Mostrar indicador de carga
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.Black
                    )
                }
            }
            
            // Mostrar mensaje de error si existe
            if (errorMessage != null && !isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = errorMessage ?: "Error desconocido",
                            color = Color.Red,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { placesViewModel.refreshPlaces() }
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            
            // Mensaje cuando no hay lugares
            if (searchResults.isEmpty() && !isLoading && errorMessage == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No hay lugares disponibles",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isNotBlank() || selectedCategory != "Todos")
                                "Intenta con otra búsqueda o categoría"
                            else
                                "Aún no hay lugares registrados",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            }
            
            // Tarjeta inferior deslizable con lugares encontrados
            if (searchResults.isNotEmpty() && !isLoading) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Handle deslizable
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.Gray)
                                .align(Alignment.CenterHorizontally)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = if (searchQuery.isBlank() && selectedCategory == "Todos")
                                stringResource(R.string.places_near_you)
                            else
                                "Resultados (${searchResults.size})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Mostrar los primeros 3 lugares
                        searchResults.take(3).forEach { place ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = place.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = place.category,
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (place.rating > 0) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = stringResource(R.string.star_icon_desc),
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "%.1f".format(place.rating),
                                            fontSize = 14.sp,
                                            color = Color.Black
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    
                                    IconButton(
                                        onClick = {
                                            if (currentUser == null) {
                                                onRequireAuth()
                                            } else {
                                                placesViewModel.toggleFavorite(place.id) { ok, msg ->
                                                    android.util.Log.d("SearchScreen", "toggleFavorite result: ok=$ok msg=$msg")
                                                }
                                            }
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (favorites.contains(place.id)) 
                                                Icons.Filled.Favorite 
                                            else 
                                                Icons.Outlined.FavoriteBorder,
                                            contentDescription = "Favorito",
                                            tint = if (favorites.contains(place.id)) Color.Red else Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                        
                        if (searchResults.size > 3) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Ver todos (${searchResults.size})",
                                fontSize = 14.sp,
                                color = Color(0xFF6200EE),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .clickable { /* Ver todos */ }
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun PlaceCardFromFirebase(
    place: co.edu.eam.unilocal.models.Place,
    onPlaceClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlaceClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Nombre y categoría
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = place.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = place.category,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                // Chip de categoría
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black
                ) {
                    Text(
                        text = place.category,
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Descripción
            Text(
                text = place.description,
                fontSize = 14.sp,
                color = Color.DarkGray,
                maxLines = 2,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Dirección
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = place.address,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
            
            // Horario
            if (place.openingTime.isNotEmpty() && place.closingTime.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🕒",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${place.openingTime} - ${place.closingTime}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
            
            // Teléfono
            if (place.phone.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📞",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = place.phone,
                        fontSize = 13.sp,
                        color = Color(0xFF6200EE),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Botón Ver más
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Ver más →",
                    fontSize = 14.sp,
                    color = Color(0xFF6200EE),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onPlaceClick() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    MyApplicationTheme {
        SearchScreen()
    }
}