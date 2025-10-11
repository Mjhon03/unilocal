package co.edu.eam.unilocal.utils

import android.util.Log
import co.edu.eam.unilocal.services.AuthService
import co.edu.eam.unilocal.services.UserService
import co.edu.eam.unilocal.models.User
import co.edu.eam.unilocal.models.UserRole

class FirebaseTest {
    
    companion object {
        private const val TAG = "FirebaseTest"
        
        suspend fun testUserRegistration() {
            Log.d(TAG, "=== INICIANDO PRUEBA DE REGISTRO ===")
            
            val authService = AuthService()
            val userService = UserService()
            
            val testEmail = "test@unilocal.com"
            val testPassword = "test123456"
            val testUser = User(
                email = testEmail,
                firstName = "Test",
                lastName = "User",
                phone = "",
                username = "testuser",
                city = "Bogotá",
                role = UserRole.USER
            )
            
            try {
                // 1. Registrar en Firebase Auth
                Log.d(TAG, "1. Registrando en Firebase Auth...")
                val authResult = authService.registerUser(testEmail, testPassword)
                
                if (authResult.isSuccess) {
                    val firebaseUser = authResult.getOrNull()!!
                    Log.d(TAG, "✅ Usuario registrado en Firebase Auth: ${firebaseUser.uid}")
                    
                    // 2. Crear usuario en Firestore
                    Log.d(TAG, "2. Creando usuario en Firestore...")
                    val userWithId = testUser.copy(id = firebaseUser.uid)
                    val userResult = userService.createUser(userWithId)
                    
                    if (userResult.isSuccess) {
                        val createdUser = userResult.getOrNull()!!
                        Log.d(TAG, "✅ Usuario creado en Firestore: ${createdUser.id}")
                        Log.d(TAG, "✅ Datos del usuario: ${createdUser.email}, ${createdUser.username}")
                        
                        // 3. Verificar que se puede leer
                        Log.d(TAG, "3. Verificando lectura desde Firestore...")
                        val readResult = userService.getUserById(createdUser.id)
                        
                        if (readResult.isSuccess) {
                            val readUser = readResult.getOrNull()
                            if (readUser != null) {
                                Log.d(TAG, "✅ Usuario leído correctamente: ${readUser.email}")
                                Log.d(TAG, "=== PRUEBA EXITOSA ===")
                            } else {
                                Log.e(TAG, "❌ Usuario no encontrado al leer")
                            }
                        } else {
                            Log.e(TAG, "❌ Error al leer usuario: ${readResult.exceptionOrNull()?.message}")
                        }
                        
                    } else {
                        Log.e(TAG, "❌ Error al crear usuario en Firestore: ${userResult.exceptionOrNull()?.message}")
                    }
                    
                } else {
                    Log.e(TAG, "❌ Error al registrar en Firebase Auth: ${authResult.exceptionOrNull()?.message}")
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error general: ${e.message}")
            }
        }
    }
}


