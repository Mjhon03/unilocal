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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.eam.unilocal.R
import co.edu.eam.unilocal.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onCallClick: () -> Unit = {},
    onMapClick: () -> Unit = {},
    onWriteReviewClick: () -> Unit = {},
    onSeeAllEventsClick: () -> Unit = {}
) {
    var isFavorite by remember { mutableStateOf(false) }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_icon_desc),
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isFavorite = !isFavorite; onFavoriteClick() }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = stringResource(R.string.favorite_icon_desc),
                            tint = if (isFavorite) Color.Red else Color.Gray
                        )
                    }
                    IconButton(onClick = onShareClick) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Compartir",
                            tint = Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Imagen principal
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    // Placeholder para imagen
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Imagen del lugar",
                        modifier = Modifier.size(80.dp),
                        tint = Color.Gray
                    )
                    
                    // Botón de fotos
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "12 ${stringResource(R.string.photos_count)}",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
            
            // Información del lugar
            item {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Nombre y rating
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.cafe_central),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "4.5 (128 ${stringResource(R.string.reviews_count)})",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = " • ",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = stringResource(R.string.cafeteria),
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = " • ",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = stringResource(R.string.price_range),
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        
                        // Estado abierto/cerrado
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text(
                                text = stringResource(R.string.open),
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Descripción
                    Text(
                        text = stringResource(R.string.cafe_description),
                        fontSize = 14.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Información de contacto
                    ContactInfoSection(
                        onCallClick = onCallClick,
                        onMapClick = onMapClick
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Botones de acción
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onMapClick,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6200EE)
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.how_to_get_there),
                                color = Color.White
                            )
                        }
                        
                        OutlinedButton(
                            onClick = onCallClick,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = stringResource(R.string.call),
                                color = Color(0xFF6200EE)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Eventos próximos
                    UpcomingEventsSection(
                        onSeeAllClick = onSeeAllEventsClick
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Reseñas
                    ReviewsSection(
                        onWriteReviewClick = onWriteReviewClick
                    )
                }
            }
        }
    }
}

@Composable
fun ContactInfoSection(
    onCallClick: () -> Unit,
    onMapClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Horarios
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = stringResource(R.string.monday_to_sunday),
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(R.string.hours_format),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
        
        // Teléfono
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Phone,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = stringResource(R.string.phone_number),
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(R.string.call_or_whatsapp),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
        
        // Dirección
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = stringResource(R.string.address),
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(R.string.see_on_map),
                    fontSize = 14.sp,
                    color = Color(0xFF6200EE),
                    modifier = Modifier.clickable { onMapClick() }
                )
            }
        }
    }
}

@Composable
fun UpcomingEventsSection(
    onSeeAllClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.upcoming_events),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onSeeAllClick() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.see_all),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Evento
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = null,
                    tint = Color(0xFF9C27B0),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.coffee_workshop),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(
                        text = stringResource(R.string.workshop_description),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF9C27B0))
                ) {
                    Text(
                        text = stringResource(R.string.upcoming),
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewsSection(
    onWriteReviewClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.reviews),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Text(
                text = stringResource(R.string.write_review),
                fontSize = 14.sp,
                color = Color(0xFF6200EE),
                modifier = Modifier.clickable { onWriteReviewClick() }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Reseñas
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ReviewItem(
                name = "María González",
                initials = "MG",
                rating = 5,
                review = stringResource(R.string.excellent_review),
                timeAgo = "2 días"
            )
            
            ReviewItem(
                name = "Carlos Rodríguez",
                initials = "CR",
                rating = 4,
                review = stringResource(R.string.good_review),
                timeAgo = "1 semana"
            )
            
            ReviewItem(
                name = "Ana Martínez",
                initials = "AM",
                rating = 5,
                review = stringResource(R.string.favorite_review),
                timeAgo = "2 semanas"
            )
        }
    }
}

@Composable
fun ReviewItem(
    name: String,
    initials: String,
    rating: Int,
    review: String,
    timeAgo: String
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                
                Text(
                    text = timeAgo,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Estrellas
            Row {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = review,
                fontSize = 14.sp,
                color = Color.Black,
                lineHeight = 20.sp
            )
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
