it adpackage co.edu.eam.unilocal.ui.screens

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import co.edu.eam.unilocal.utils.LoginValidationState
import co.edu.eam.unilocal.viewmodels.AuthViewModel
import co.edu.eam.unilocal.R
import co.edu.eam.unilocal.navigation.RouteScreen
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var passwordVisible by remember { mutableStateOf(false) }
    
    // Estado de validación del formulario
    val validationState = remember { LoginValidationState() }
    
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
    
    // Variable para rastrear si el usuario se autenticó en esta sesión
    var hasAuthenticatedInSession by remember { mutableStateOf(false) }
    
    // Navegar automáticamente solo si el usuario se autenticó en esta sesión
    LaunchedEffect(authState) {
        if (authState is co.edu.eam.unilocal.viewmodels.AuthState.Authenticated && hasAuthenticatedInSession) {
            onLoginClick()
        }
    }
    
    // Limpiar errores cuando cambie el email o contraseña
    LaunchedEffect(validationState.email.value, validationState.password.value) {
        if (errorMessage != null) {
            authViewModel.clearError()
        }
    }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.login_tab),
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
        // Main content column with scroll
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
            
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, tab ->
                    SegmentedButton(
                        onClick = { 
                            selectedTab = index
                            if (index == 1) {
                                onRegisterClick()
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
            
            Text(
                text = stringResource(R.string.welcome_uniLocal),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = stringResource(R.string.login_subtitle),
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            
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
                        validationState.updateEmail(it)
                    },
                    placeholder = { Text(stringResource(R.string.email_placeholder)) },
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
                        validationState.updatePassword(it)
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
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Botón de Login
            Button(
                onClick = {
                    keyboardController?.hide()
                    if (validationState.validateForm().isValid) {
                        hasAuthenticatedInSession = true
                        authViewModel.signInUser(
                            validationState.email.value,
                            validationState.password.value
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
                        text = stringResource(R.string.login_button),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(onClick = onForgotPasswordClick) {
                Text(
                    text = stringResource(R.string.forgot_password),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MyApplicationTheme {
        LoginScreen(
            navController = rememberNavController()
        ) // modifier will use its default value here
    }
}
