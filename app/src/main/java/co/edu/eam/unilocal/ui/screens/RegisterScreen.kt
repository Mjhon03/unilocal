package co.edu.eam.unilocal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.eam.unilocal.ui.theme.MyApplicationTheme
import co.edu.eam.unilocal.utils.FormField
import co.edu.eam.unilocal.utils.FormValidationState
import co.edu.eam.unilocal.viewmodels.AuthViewModel
import co.edu.eam.unilocal.R
import co.edu.eam.unilocal.navigation.RouteScreen
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(1) } // 1 = Registrarse seleccionado
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    // Estado de validación del formulario
    val validationState = remember { FormValidationState() }
    
    // Focus requesters para marcar campos como tocados
    val firstNameFocusRequester = remember { FocusRequester() }
    val lastNameFocusRequester = remember { FocusRequester() }
    val usernameFocusRequester = remember { FocusRequester() }
    val emailFocusRequester = remember { FocusRequester() }
    val cityFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val confirmPasswordFocusRequester = remember { FocusRequester() }
    
    // Estados del ViewModel
    val authState by authViewModel.authState.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    
    // Controlador del teclado
    val keyboardController = LocalSoftwareKeyboardController.current
    
    val tabs = listOf(
        stringResource(R.string.login_tab), 
        stringResource(R.string.register_tab)
    )
    
    // Variable para rastrear si el usuario se registró en esta sesión
    var hasRegisteredInSession by remember { mutableStateOf(false) }
    
    // Navegar automáticamente solo si el usuario se registró en esta sesión
    LaunchedEffect(authState) {
        if (authState is co.edu.eam.unilocal.viewmodels.AuthState.Authenticated && hasRegisteredInSession) {
            onRegisterClick()
        }
    }
    
    // Limpiar errores cuando cambien los campos
    LaunchedEffect(
        validationState.firstName.value,
        validationState.lastName.value,
        validationState.username.value,
        validationState.email.value,
        validationState.city.value,
        validationState.password.value,
        validationState.confirmPassword.value
    ) {
        if (errorMessage != null) {
            authViewModel.clearError()
        }
    }
    
    // Función para marcar todos los campos como tocados
    fun touchAllFields() {
        validationState.touchField(FormField.FIRST_NAME)
        validationState.touchField(FormField.LAST_NAME)
        validationState.touchField(FormField.USERNAME)
        validationState.touchField(FormField.EMAIL)
        validationState.touchField(FormField.CITY)
        validationState.touchField(FormField.PASSWORD)
        validationState.touchField(FormField.CONFIRM_PASSWORD)
    }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.register_tab),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigate(RouteScreen.Search) }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.systemBars),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Selector de pestañas
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, tab ->
                    SegmentedButton(
                        onClick = { 
                            selectedTab = index
                            if (index == 0) {
                                onLoginClick()
                            }
                        },
                        selected = selectedTab == index,
                        shape = RoundedCornerShape(8.dp),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = Color.White,
                            activeContentColor = Color.Black,
                            inactiveContainerColor = Color(0xFFF5F5F5),
                            inactiveContentColor = Color.Gray
                        )
                    ) {
                        Text(tab)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Mostrar error general si existe
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
            
            // Mensaje de bienvenida
            Text(
                text = stringResource(R.string.create_account),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = stringResource(R.string.register_subtitle),
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Campos de formulario
            // Nombre y Apellido en la misma fila
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Campo de Nombre
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.first_name),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = validationState.firstName.value,
                        onValueChange = { 
                            validationState.updateField(FormField.FIRST_NAME, it)
                        },
                        placeholder = { Text(stringResource(R.string.first_name_placeholder)) },
                        isError = validationState.firstName.error != null,
                        supportingText = if (validationState.firstName.error != null) {
                            { Text(text = validationState.firstName.error!!, color = MaterialTheme.colorScheme.error) }
                        } else null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                
                // Campo de Apellido
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.last_name),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = validationState.lastName.value,
                        onValueChange = { 
                            validationState.updateField(FormField.LAST_NAME, it)
                        },
                        placeholder = { Text(stringResource(R.string.last_name_placeholder)) },
                        isError = validationState.lastName.error != null,
                        supportingText = if (validationState.lastName.error != null) {
                            { Text(text = validationState.lastName.error!!, color = MaterialTheme.colorScheme.error) }
                        } else null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Campo de Usuario
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.username),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = validationState.username.value,
                    onValueChange = { 
                        validationState.updateField(FormField.USERNAME, it)
                    },
                    placeholder = { Text(stringResource(R.string.username_placeholder)) },
                    isError = validationState.username.error != null,
                    supportingText = if (validationState.username.error != null) {
                        { Text(text = validationState.username.error!!, color = MaterialTheme.colorScheme.error) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Campo de Email
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.email),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = validationState.email.value,
                    onValueChange = { 
                        validationState.updateField(FormField.EMAIL, it)
                    },
                    placeholder = { Text(stringResource(R.string.email_register_placeholder)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = validationState.email.error != null,
                    supportingText = if (validationState.email.error != null) {
                        { Text(text = validationState.email.error!!, color = MaterialTheme.colorScheme.error) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Campo de Ciudad
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.city),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = validationState.city.value,
                    onValueChange = { 
                        validationState.updateField(FormField.CITY, it)
                    },
                    placeholder = { Text(stringResource(R.string.city_placeholder)) },
                    isError = validationState.city.error != null,
                    supportingText = if (validationState.city.error != null) {
                        { Text(text = validationState.city.error!!, color = MaterialTheme.colorScheme.error) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Campo de Contraseña
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.password),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = validationState.password.value,
                    onValueChange = { 
                        validationState.updateField(FormField.PASSWORD, it)
                    },
                    placeholder = { Text(stringResource(R.string.password_placeholder)) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = validationState.password.error != null,
                    supportingText = if (validationState.password.error != null) {
                        { Text(text = validationState.password.error!!, color = MaterialTheme.colorScheme.error) }
                    } else null,
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Campo de Confirmar Contraseña
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.confirm_password),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = validationState.confirmPassword.value,
                    onValueChange = { 
                        validationState.updateField(FormField.CONFIRM_PASSWORD, it)
                    },
                    placeholder = { Text(stringResource(R.string.password_placeholder)) },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = validationState.confirmPassword.error != null,
                    supportingText = if (validationState.confirmPassword.error != null) {
                        { Text(text = validationState.confirmPassword.error!!, color = MaterialTheme.colorScheme.error) }
                    } else null,
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (confirmPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Botón de crear cuenta
            Button(
                onClick = {
                    keyboardController?.hide()
                    // Marcar todos los campos como tocados para mostrar errores
                    touchAllFields()
                    
                    if (validationState.validateForm().isValid) {
                        hasRegisteredInSession = true
                        authViewModel.registerUser(
                            email = validationState.email.value,
                            password = validationState.password.value,
                            firstName = validationState.firstName.value,
                            lastName = validationState.lastName.value,
                            username = validationState.username.value,
                            city = validationState.city.value,
                            confirmPassword = validationState.confirmPassword.value
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading && validationState.isFormValid
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.create_account_button),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Disclaimer
            TextButton(onClick = onTermsClick) {
                Text(
                    text = stringResource(R.string.terms_conditions),
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    MyApplicationTheme {
        RegisterScreen(
            navController = rememberNavController()
        )
    }
}
