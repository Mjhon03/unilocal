package co.edu.eam.unilocal.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.eam.unilocal.models.User
import co.edu.eam.unilocal.models.UserRole
import co.edu.eam.unilocal.services.AuthService
import co.edu.eam.unilocal.services.UserService
import co.edu.eam.unilocal.services.AdminService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val user: User) : AuthState()
    object Unauthenticated : AuthState()
}

class AuthViewModel : ViewModel() {
    
    private val authService = AuthService()
    private val userService = UserService()
    private val adminService = AdminService()
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        checkAuthState()
    }
    
    private fun checkAuthState() {
        viewModelScope.launch {
            try {
                val firebaseUser = authService.getCurrentUser()
                if (firebaseUser != null) {
                    // Cargar datos del usuario desde Firestore
                    val userResult = userService.getUserById(firebaseUser.uid)
                    if (userResult.isSuccess) {
                        val user = userResult.getOrNull()
                        if (user != null) {
                            _currentUser.value = user
                            _authState.value = AuthState.Authenticated(user)
                        } else {
                            _authState.value = AuthState.Unauthenticated
                        }
                    } else {
                        _authState.value = AuthState.Unauthenticated
                    }
                } else {
                    _authState.value = AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al verificar estado de autenticación: ${e.message}")
                _authState.value = AuthState.Unauthenticated
            }
        }
    }
    
    fun registerUser(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        username: String,
        city: String,
        confirmPassword: String = password
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                // Validaciones
                if (firstName.isBlank() || lastName.isBlank() || username.isBlank() || 
                    email.isBlank() || city.isBlank() || password.isBlank()) {
                    _errorMessage.value = "Todos los campos son obligatorios"
                    _isLoading.value = false
                    return@launch
                }
                
                if (password.length < 6) {
                    _errorMessage.value = "La contraseña debe tener al menos 6 caracteres"
                    _isLoading.value = false
                    return@launch
                }
                
                if (password != confirmPassword) {
                    _errorMessage.value = "Las contraseñas no coinciden"
                    _isLoading.value = false
                    return@launch
                }
                
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    _errorMessage.value = "Ingresa un email válido"
                    _isLoading.value = false
                    return@launch
                }
                
                Log.d("AuthViewModel", "Iniciando registro para: $email")
                
                // 1. Crear usuario en Firebase Authentication
                val authResult = authService.registerUser(email, password)
                if (authResult.isFailure) {
                    _errorMessage.value = authResult.exceptionOrNull()?.message ?: "Error al registrar usuario"
                    _isLoading.value = false
                    return@launch
                }
                
                val firebaseUser = authResult.getOrNull()!!
                Log.d("AuthViewModel", "Usuario creado en Firebase Auth: ${firebaseUser.uid}")
                
                // 2. Determinar el rol del usuario
                val userRole = if (adminService.isAdminEmail(email)) {
                    Log.d("AuthViewModel", "Email de administrador detectado: $email")
                    UserRole.ADMIN
                } else {
                    UserRole.USER
                }
                
                // 3. Crear usuario en Firestore
                val user = User(
                    id = firebaseUser.uid,
                    email = email,
                    firstName = firstName,
                    lastName = lastName,
                    phone = "",
                    username = username,
                    city = city,
                    role = userRole
                )
                
                val userResult = userService.createUser(user)
                if (userResult.isFailure) {
                    _errorMessage.value = userResult.exceptionOrNull()?.message ?: "Error al crear perfil de usuario"
                    _isLoading.value = false
                    return@launch
                }
                
                val createdUser = userResult.getOrNull()!!
                Log.d("AuthViewModel", "Usuario creado en Firestore: ${createdUser.id} con rol: ${createdUser.role}")
                
                // 4. Actualizar estado
                _currentUser.value = createdUser
                _authState.value = AuthState.Authenticated(createdUser)
                _isLoading.value = false
                
                Log.d("AuthViewModel", "Registro completado exitosamente")
                
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error en registro: ${e.message}")
                _errorMessage.value = e.message ?: "Error desconocido"
                _isLoading.value = false
            }
        }
    }
    
    fun signInUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                Log.d("AuthViewModel", "Iniciando login para: $email")
                
                // 1. Autenticar con Firebase
                val authResult = authService.signInUser(email, password)
                if (authResult.isFailure) {
                    _errorMessage.value = authResult.exceptionOrNull()?.message ?: "Error al iniciar sesión"
                    _isLoading.value = false
                    return@launch
                }
                
                val firebaseUser = authResult.getOrNull()!!
                Log.d("AuthViewModel", "Usuario autenticado: ${firebaseUser.uid}")
                
                // 2. Cargar datos del usuario desde Firestore
                val userResult = userService.getUserById(firebaseUser.uid)
                if (userResult.isFailure) {
                    _errorMessage.value = "Error al cargar datos del usuario"
                    _isLoading.value = false
                    return@launch
                }
                
                val user = userResult.getOrNull()
                if (user != null) {
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated(user)
                    Log.d("AuthViewModel", "Login completado exitosamente")
                } else {
                    _errorMessage.value = "Usuario no encontrado en la base de datos"
                }
                
                _isLoading.value = false
                
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error en login: ${e.message}")
                _errorMessage.value = e.message ?: "Error desconocido"
                _isLoading.value = false
            }
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            try {
                authService.signOut()
                _currentUser.value = null
                _authState.value = AuthState.Unauthenticated
                _errorMessage.value = null
                Log.d("AuthViewModel", "Usuario cerró sesión")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al cerrar sesión: ${e.message}")
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }

    fun updateUserProfile(updatedUser: User) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val result = userService.updateUser(updatedUser)
                if (result.isSuccess) {
                    val user = result.getOrNull()!!
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated(user)
                    Log.d("AuthViewModel", "Usuario actualizado exitosamente: ${user.id}")
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Error al actualizar usuario"
                }

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al actualizar perfil: ${e.message}")
                _errorMessage.value = e.message ?: "Error desconocido"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun isUserModerator(): Boolean {
        return _currentUser.value?.role == UserRole.MODERATOR
    }
    
    fun isUserAdmin(): Boolean {
        return _currentUser.value?.role == UserRole.ADMIN
    }
}
