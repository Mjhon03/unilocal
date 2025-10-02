package co.edu.eam.unilocal.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.eam.unilocal.ui.theme.MyApplicationTheme
import co.edu.eam.unilocal.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacesListScreen(
    modifier: Modifier = Modifier,
    onCrearClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onPlaceDetailClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todos") }
    var isSearchActive by remember { mutableStateOf(false) }
    
    val filters = listOf(
        "Todos", "Abierto ahora", "Mejor calificados", "Cerca de mí", "Nuevos"
    )
    
    val places = listOf(
        PlaceItem(
            name = "Café Central",
            description = "Café acogedor en el centro de la ciudad con especialidades locales.",
            rating = 4.5f,
            reviews = 128,
            hours = "8:00 AM - 10:00 PM",
            distance = "0.2 km",
            isOpen = true
        ),
        PlaceItem(
            name = "Restaurante El Fogón",
            description = "Comida tradicional regional con ambiente familiar.",
            rating = 4.8f,
            reviews = 256,
            hours = "12:00 PM - 11:00 PM",
            distance = "0.5 km",
            isOpen = true
        ),
        PlaceItem(
            name = "Hotel Plaza",
            description = "Hotel boutique en el corazón de la ciudad.",
            rating = 4.3f,
            reviews = 89,
            hours = "24 horas",
            distance = "1.2 km",
            isOpen = true
        ),
        PlaceItem(
            name = "Museo de Arte Moderno",
            description = "Exposiciones de arte contemporáneo y cultura local.",
            rating = 4.6f,
            reviews = 45,
            hours = "9:00 AM - 6:00 PM",
            distance = "2.1 km",
            isOpen = false
        )
    )
    
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
                    placeholder = { Text(stringResource(R.string.search_what)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.search_icon_desc)
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { /* Acción del filtro */ }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Filtros"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Contenido cuando la búsqueda está activa
                    LazyColumn {
                        items(5) { index ->
                            Text(
                                text = "Resultado de búsqueda ${index + 1}",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Chips de filtros
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filters) { filter ->
                        FilterChip(
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) },
                            selected = selectedFilter == filter,
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
                    // Mapa
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .clickable { onBackClick() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = stringResource(R.string.map_icon_desc),
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.map),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    
                    // Buscar (activo)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.search_icon_desc),
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.search),
                            color = Color.White,
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
                            imageVector = Icons.Default.Add,
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
                            imageVector = Icons.Default.Favorite,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            // Contador de resultados
            Text(
                text = "4 ${stringResource(R.string.places_found)}",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            // Lista de lugares
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
            ) {
                items(places) { place ->
                    PlaceCard(
                        place = place,
                        onSeeMoreClick = onPlaceDetailClick
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

data class PlaceItem(
    val name: String,
    val description: String,
    val rating: Float,
    val reviews: Int,
    val hours: String,
    val distance: String,
    val isOpen: Boolean
)

@Composable
fun PlaceCard(
    place: PlaceItem,
    onSeeMoreClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Imagen placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Imagen del lugar",
                    tint = Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Información del lugar
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = place.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = place.description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${place.rating} (${place.reviews})",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Horarios
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = place.hours,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Distancia
                Text(
                    text = place.distance,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            // Estado y botón
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Estado abierto/cerrado
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (place.isOpen) Color(0xFF4CAF50) else Color(0xFFE0E0E0)
                    )
                ) {
                    Text(
                        text = if (place.isOpen) stringResource(R.string.open) else stringResource(R.string.closed),
                        color = if (place.isOpen) Color.White else Color.Black,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Botón ver más
                Text(
                    text = stringResource(R.string.see_more),
                    fontSize = 14.sp,
                    color = Color(0xFF6200EE),
                    modifier = Modifier.clickable { onSeeMoreClick() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlacesListScreenPreview() {
    MyApplicationTheme {
        PlacesListScreen()
    }
}
