package com.example.myapplication.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.myapplication.R
import com.example.myapplication.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onCrearClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todos") }
    var isSearchActive by remember { mutableStateOf(false) }
    
    val categories = listOf(
        "Todos", "Restaurantes", "Cafeterías", "Hoteles", "Museos"
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
                    placeholder = { Text(stringResource(R.string.search_placeholder)) },
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
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = stringResource(R.string.filters_icon_desc)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Contenido cuando la búsqueda está activa
                    LazyColumn {
                        items(5) { index ->
                            Text(
                                text = stringResource(R.string.search_result, index),
                                modifier = Modifier.padding(16.dp)
                            )
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
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = stringResource(R.string.map_icon_desc),
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.map),
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                    
                    // Buscar
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)

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
        ) {
            // Área del mapa
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFF5F5F5)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = stringResource(R.string.map_icon_desc),
                            modifier = Modifier.size(48.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.interactive_map),
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                        Text(
                            text = stringResource(R.string.map_description),
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                    
                    // Marcadores simulados
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                            .align(Alignment.TopStart)
                            .padding(top = 100.dp, start = 80.dp)
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color.Blue)
                            .align(Alignment.TopEnd)
                            .padding(top = 120.dp, end = 100.dp)
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color.Green)
                            .align(Alignment.BottomStart)
                            .padding(bottom = 200.dp, start = 120.dp)
                    )
                }
            }
            
            // Tarjeta inferior deslizable
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
                        text = stringResource(R.string.places_near_you),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Elemento de lugar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.cafe_central),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                            Text(
                                text = stringResource(R.string.cafe_category),
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                        
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = stringResource(R.string.star_icon_desc),
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = stringResource(R.string.rating),
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                            }
                            Text(
                                text = stringResource(R.string.view),
                                fontSize = 14.sp,
                                color = Color(0xFF6200EE),
                                modifier = Modifier.clickable { /* Acción ver */ }
                            )
                        }
                    }
                }
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
