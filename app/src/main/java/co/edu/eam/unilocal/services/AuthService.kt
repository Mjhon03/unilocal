package co.edu.eam.unilocal.services

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
class AuthService {
    
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    
    suspend fun registerUser(
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            Log.d("AuthService", "Iniciando registro para: $email")
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Log.d("AuthService", "Usuario registrado exitosamente: ${user.uid}")
                Result.success(user)
            } else {
                Log.e("AuthService", "Error: Usuario es null después del registro")
                Result.failure(Exception("Error al crear usuario"))
            }
        } catch (e: Exception) {
            Log.e("AuthService", "Error en registro: ${e.message}")
            Result.failure(e)
        }
    }
    
    suspend fun signInUser(
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            Log.d("AuthService", "Iniciando login para: $email")
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Log.d("AuthService", "Usuario autenticado exitosamente: ${user.uid}")
                Result.success(user)
            } else {
                Log.e("AuthService", "Error: Usuario es null después del login")
                Result.failure(Exception("Error al autenticar usuario"))
            }
        } catch (e: Exception) {
            Log.e("AuthService", "Error en login: ${e.message}")
            Result.failure(e)
        }
    }
    
    fun signOut() {
        auth.signOut()
        Log.d("AuthService", "Usuario cerró sesión")
    }
    
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
    
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            Log.d("AuthService", "Enviando correo de recuperación a: $email")
            auth.sendPasswordResetEmail(email).await()
            Log.d("AuthService", "Correo de recuperación enviado exitosamente")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthService", "Error al enviar correo de recuperación: ${e.message}")
            Result.failure(e)
        }
    }
}

