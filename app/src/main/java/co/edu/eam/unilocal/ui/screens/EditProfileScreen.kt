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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.eam.unilocal.ui.theme.MyApplicationTheme
import co.edu.eam.unilocal.models.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    currentUser: User? = null,
    isLoading: Boolean = false,
    onBackClick: () -> Unit = {},
    onSaveClick: (updatedUser: User) -> Unit = {},
    onChangePhotoClick: () -> Unit = {}
) {
    // Inicializar valores a partir del usuario actual si existe
    val originalFirstName = currentUser?.firstName ?: ""
    val originalLastName = currentUser?.lastName ?: ""
    val originalEmail = currentUser?.email ?: ""
    // The User model currently doesn't include a phone field.
    // Keep originalPhone empty so the field behaves as editable placeholder when absent.
    val originalPhone = ""
    val originalCity = currentUser?.city ?: ""

    var firstName by remember { mutableStateOf(originalFirstName) }
    var lastName by remember { mutableStateOf(originalLastName) }
    var fullName by remember { mutableStateOf(if (originalFirstName.isNotBlank() || originalLastName.isNotBlank()) "${originalFirstName} ${originalLastName}" else "Usuario Demo") }
    var email by remember { mutableStateOf(originalEmail.ifBlank { "example@gmail.com" }) }
    var phone by remember { mutableStateOf(originalPhone) }
    var location by remember { mutableStateOf(originalCity.ifBlank { "Ciudad, País" }) }

    // Snapshot de los valores guardados (se actualiza cuando currentUser cambia)
    var savedFirstName by remember { mutableStateOf(originalFirstName) }
    var savedLastName by remember { mutableStateOf(originalLastName) }
    var savedEmail by remember { mutableStateOf(originalEmail) }
    var savedPhone by remember { mutableStateOf(originalPhone) }
    var savedLocation by remember { mutableStateOf(originalCity) }

    // Si el currentUser cambia (p. ej. tras guardar), actualizar los estados locales y el snapshot
    androidx.compose.runtime.LaunchedEffect(currentUser) {
        val ofn = currentUser?.firstName ?: ""
        val oln = currentUser?.lastName ?: ""
        firstName = ofn
        lastName = oln
        fullName = if (ofn.isNotBlank() || oln.isNotBlank()) "$ofn $oln" else "Usuario Demo"
        email = currentUser?.email ?: "example@gmail.com"
        location = currentUser?.city ?: "Ciudad, País"
        // phone queda como estaba si el modelo no lo provee

        // Actualizar snapshot guardado para que isDirty vuelva a false después del guardado
        savedFirstName = ofn
        savedLastName = oln
        savedEmail = currentUser?.email ?: ""
        savedLocation = currentUser?.city ?: ""
        savedPhone = phone // si quieres que phone también se considere guardado, actualiza aquí cuando lo persistamos
    }

    // Detectar si hay cambios respecto al snapshot guardado (se recalcula cuando `currentUser` o campos locales cambian)
    val isDirty = remember(currentUser, firstName, lastName, email, phone, location, savedFirstName, savedLastName, savedEmail, savedPhone, savedLocation) {
        firstName != savedFirstName ||
        lastName != savedLastName ||
        email != savedEmail ||
        phone != savedPhone ||
        location != savedLocation
    }
    
    Scaffold(
        modifier = modifier,
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
                        contentDescription = "Volver",
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Editar perfil",
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
            
            // Sección Foto de perfil
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Foto de perfil",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Icono de perfil con cámara
                            Box(
                                modifier = Modifier.size(80.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Fondo del icono de perfil
                                Surface(
                                    modifier = Modifier.size(80.dp),
                                    shape = CircleShape,
                                    color = Color(0xFFF5F5F5)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Perfil",
                                            modifier = Modifier.size(40.dp),
                                            tint = Color.Gray
                                        )
                                    }
                                }
                                
                                // Icono de cámara superpuesto
                                Surface(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.BottomEnd),
                                    shape = CircleShape,
                                    color = Color(0xFF6200EE)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Cámara",
                                            modifier = Modifier.size(12.dp),
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column {
                                Text(
                                    text = "Cambiar foto",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black,
                                    modifier = Modifier.clickable { onChangePhotoClick() }
                                )
                                Text(
                                    text = "JPG, PNG o GIF. Máximo 5MB.",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
            
            // Sección Información personal
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Información personal",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Nombre completo
                        Column {
                            Text(
                                text = "Nombre completo",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = {
                                    fullName = it
                                    // Mantener first/last separados si el usuario edita
                                    val parts = it.split(" ")
                                    firstName = parts.firstOrNull() ?: ""
                                    lastName = parts.drop(1).joinToString(" ")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.Gray,
                                    unfocusedTextColor = Color.Gray
                                )
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Email
                        Column {
                            Text(
                                text = "Email",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.Gray,
                                    unfocusedTextColor = Color.Gray,
                                    disabledTextColor = Color.Gray,
                                    disabledBorderColor = Color(0xFFE0E0E0)
                                ),
                                enabled = false
                            )
                            Text(
                                text = "El email no se puede modificar",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Teléfono
                        Column {
                            Text(
                                text = "Teléfono",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.Gray,
                                    unfocusedTextColor = Color.Gray
                                ),
                                placeholder = { Text(text = "+57 300 123 4567", color = Color.Gray) }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Ubicación
                        Column {
                            Text(
                                text = "Ubicación",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = location,
                                onValueChange = { location = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.Gray,
                                    unfocusedTextColor = Color.Gray
                                )
                            )
                        }
                    }
                }
            }
            
            // Sección Información de la cuenta
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Información de la cuenta",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Tipo de cuenta
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tipo de cuenta",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                            Text(
                                text = "Usuario",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                            // Miembro desde
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Miembro desde",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                            val memberSinceText = remember(currentUser) {
                                currentUser?.createdAt?.let { createdAtMillis ->
                                    try {
                                        val sdf = java.text.SimpleDateFormat("MMMM 'de' yyyy", java.util.Locale("es"))
                                        sdf.format(java.util.Date(createdAtMillis))
                                    } catch (e: Exception) {
                                        ""
                                    }
                                } ?: ""
                            }

                            Text(
                                text = if (memberSinceText.isNotBlank()) memberSinceText else "-",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Botón Guardar cambios
            item {
                if (isLoading) {
                    // Mostrar indicador de carga simple
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Guardando...", color = Color.White)
                    }
                } else {
                    Button(
                        onClick = {
                            // Construir usuario actualizado y pasar al callback
                            val updated = User(
                                id = currentUser?.id ?: "",
                                email = email,
                                firstName = firstName,
                                phone = phone,
                                lastName = lastName,
                                username = currentUser?.username ?: "",
                                city = location,
                                profileImageUrl = currentUser?.profileImageUrl ?: "",
                                createdAt = currentUser?.createdAt ?: System.currentTimeMillis(),
                                isActive = currentUser?.isActive ?: true,
                                role = currentUser?.role ?: co.edu.eam.unilocal.models.UserRole.USER
                            )
                            onSaveClick(updated)
                        },
                        enabled = isDirty,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isDirty) Color.Black else Color.LightGray),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Guardar cambios",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    MyApplicationTheme {
        EditProfileScreen()
    }
}
