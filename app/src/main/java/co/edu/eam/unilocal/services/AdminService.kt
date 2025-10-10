package co.edu.eam.unilocal.services

import android.util.Log
import co.edu.eam.unilocal.models.User
import co.edu.eam.unilocal.models.UserRole
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

class AdminService {
    
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")
    private val adminsCollection = firestore.collection("admins")
    
    // Lista de emails de administradores predefinidos
    private val adminEmails = listOf(
        "admin@unilocal.com",
        "administrador@unilocal.com",
        "moderador@unilocal.com"
    )
    
    /**
     * Verifica si un email pertenece a un administrador
     */
    fun isAdminEmail(email: String): Boolean {
        return adminEmails.contains(email.lowercase())
    }
    
    /**
     * Crea un usuario administrador
     */
    suspend fun createAdminUser(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        username: String,
        city: String
    ): Result<User> {
        return try {
            Log.d("AdminService", "Creando usuario administrador: $email")
            
            // Verificar si el email es de administrador
            if (!isAdminEmail(email)) {
                return Result.failure(Exception("Este email no está autorizado para ser administrador"))
            }
            
            // Crear usuario con rol de administrador
            val adminUser = User(
                id = "", // Se asignará después de crear en Firebase Auth
                email = email,
                firstName = firstName,
                lastName = lastName,
                username = username,
                city = city,
                role = UserRole.ADMIN
            )
            
            Result.success(adminUser)
            
        } catch (e: Exception) {
            Log.e("AdminService", "Error al crear administrador: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Promueve un usuario existente a administrador
     */
    suspend fun promoteToAdmin(userId: String): Result<User> {
        return try {
            Log.d("AdminService", "Promoviendo usuario a administrador: $userId")
            
            val userResult = UserService().getUserById(userId)
            if (userResult.isFailure) {
                return Result.failure(Exception("Usuario no encontrado"))
            }
            
            val user = userResult.getOrNull() ?: return Result.failure(Exception("Usuario no encontrado"))
            
            // Verificar si el email es de administrador
            if (!isAdminEmail(user.email)) {
                return Result.failure(Exception("Este email no está autorizado para ser administrador"))
            }
            
            val updatedUser = user.copy(role = UserRole.ADMIN)
            val updateResult = UserService().updateUser(updatedUser)
            
            if (updateResult.isSuccess) {
                Log.d("AdminService", "Usuario promovido a administrador exitosamente")
                Result.success(updatedUser)
            } else {
                Result.failure(updateResult.exceptionOrNull() ?: Exception("Error al actualizar usuario"))
            }
            
        } catch (e: Exception) {
            Log.e("AdminService", "Error al promover a administrador: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene todos los administradores
     */
    suspend fun getAllAdmins(): Result<List<User>> {
        return try {
            Log.d("AdminService", "Obteniendo todos los administradores")
            
            val query = usersCollection
                .whereIn("role", listOf(UserRole.ADMIN.name, UserRole.MODERATOR.name))
                .get()
                .await()
            
            val admins = query.documents.mapNotNull { document ->
                document.toObject(User::class.java)
            }
            
            Log.d("AdminService", "Encontrados ${admins.size} administradores")
            Result.success(admins)
            
        } catch (e: Exception) {
            Log.e("AdminService", "Error al obtener administradores: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Verifica si un usuario es administrador
     */
    suspend fun isUserAdmin(userId: String): Result<Boolean> {
        return try {
            val userResult = UserService().getUserById(userId)
            if (userResult.isSuccess) {
                val user = userResult.getOrNull()
                val isAdmin = user?.role == UserRole.ADMIN || user?.role == UserRole.MODERATOR
                Result.success(isAdmin)
            } else {
                Result.success(false)
            }
        } catch (e: Exception) {
            Log.e("AdminService", "Error al verificar si es administrador: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene la lista de emails de administradores autorizados
     */
    fun getAuthorizedAdminEmails(): List<String> {
        return adminEmails
    }
}
