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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.eam.unilocal.R
import co.edu.eam.unilocal.models.ModerationPlace
import co.edu.eam.unilocal.models.ModerationStatus
import co.edu.eam.unilocal.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModerationPanelScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onApprovePlace: (String) -> Unit = {},
    onRejectPlace: (String) -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pendientes", "Historial")
    
    // Datos de ejemplo para lugares pendientes
    val pendingPlaces = remember {
        listOf(
            ModerationPlace(
                id = "1",
                name = "Tienda Artesanal Raíces",
                description = "Artesanías y productos locales únicos.",
                address = "Calle 9 #25-10, La Candelaria",
                submittedBy = "Juan Pérez",
                phone = "+57 4 345 6789",
                imageUrl = "",
                createdAt = "1 mar 2024"
            ),
            ModerationPlace(
                id = "2",
                name = "Hotel Boutique Colonial",
                description = "Hospedaje de lujo en edificio colonial restaurado.",
                address = "Carrera 22 #8-42, Centro",
                submittedBy = "Carlos Rodríguez",
                phone = "+57 4 456 7890",
                website = "www.hotelcolonial.com",
                imageUrl = "",
                createdAt = "2 mar 2024"
            )
        )
    }
    
    val historyPlaces = remember {
        listOf(
            ModerationPlace(
                id = "3",
                name = "Restaurante El Fogón",
                description = "Comida tradicional regional.",
                address = "Calle 15 #8-20, Centro",
                submittedBy = "María García",
                phone = "+57 4 123 4567",
                imageUrl = "",
                createdAt = "28 feb 2024",
                status = ModerationStatus.APPROVED
            )
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            text = "Panel de Moderación",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Gestiona la aprobación de lugares",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            // Tabs
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.padding(16.dp)
            ) {
                tabs.forEachIndexed { index, tab ->
                    SegmentedButton(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(tab)
                            if (index == 0 && pendingPlaces.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(
                                            Color.Red,
                                            RoundedCornerShape(10.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = pendingPlaces.size.toString(),
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Contador de lugares pendientes
            if (selectedTab == 0) {
                Text(
                    text = "${pendingPlaces.size} lugares pendientes de aprobación",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Lista de lugares
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val places = if (selectedTab == 0) pendingPlaces else historyPlaces
                
                items(places) { place ->
                    ModerationPlaceCard(
                        place = place,
                        onApprove = { onApprovePlace(place.id) },
                        onReject = { onRejectPlace(place.id) },
                        showActions = selectedTab == 0
                    )
                }
            }
        }
    }
}

@Composable
fun ModerationPlaceCard(
    place: ModerationPlace,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    showActions: Boolean = true
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Imagen del lugar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Imagen del lugar",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Información del lugar
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = place.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Estado
                    Box(
                        modifier = Modifier
                            .background(
                                when (place.status) {
                                    ModerationStatus.PENDING -> MaterialTheme.colorScheme.surfaceVariant
                                    ModerationStatus.APPROVED -> Color(0xFF4CAF50)
                                    ModerationStatus.REJECTED -> Color(0xFFF44336)
                                },
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = when (place.status) {
                                ModerationStatus.PENDING -> "Pendiente"
                                ModerationStatus.APPROVED -> "Aprobado"
                                ModerationStatus.REJECTED -> "Rechazado"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = when (place.status) {
                                ModerationStatus.PENDING -> MaterialTheme.colorScheme.onSurfaceVariant
                                ModerationStatus.APPROVED -> Color.White
                                ModerationStatus.REJECTED -> Color.White
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Ubicación
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Ubicación",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = place.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Enviado por
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Usuario",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Por ${place.submittedBy}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Descripción
                Text(
                    text = place.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Información de contacto
                Column {
                    place.phone?.let { phone ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Phone,
                                contentDescription = "Teléfono",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = phone,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    place.website?.let { website ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                Icon(
                    imageVector = Icons.Filled.Language,
                    contentDescription = "Sitio web",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = website,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Fecha de creación
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = "Fecha",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Creado: ${place.createdAt}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Botones de acción
                if (showActions) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onApprove,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Aprobar",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Aprobar")
                        }
                        
                        Button(
                            onClick = onReject,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF44336)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Rechazar",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Rechazar")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ModerationPanelScreenPreview() {
    MyApplicationTheme {
        ModerationPanelScreen()
    }
}
