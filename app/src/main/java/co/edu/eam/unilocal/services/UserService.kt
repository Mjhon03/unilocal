package co.edu.eam.unilocal.services

import android.util.Log
import co.edu.eam.unilocal.models.User
import co.edu.eam.unilocal.models.UserRole
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
class UserService {
    
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")
    
    suspend fun createUser(user: User): Result<User> {
        return try {
            Log.d("UserService", "Creando usuario en Firestore: ${user.email}")
            
            // Verificar si el username ya existe
            val usernameExists = checkUsernameExists(user.username)
            if (usernameExists) {
                Log.e("UserService", "Username ya existe: ${user.username}")
                return Result.failure(Exception("El nombre de usuario ya está en uso"))
            }
            
            // Verificar si el email ya existe
            val emailExists = checkEmailExists(user.email)
            if (emailExists) {
                Log.e("UserService", "Email ya existe: ${user.email}")
                return Result.failure(Exception("El email ya está registrado"))
            }
            
            // Crear el documento con el ID del usuario de Firebase Auth
            val userWithId = user.copy(id = user.id.ifEmpty { "temp_${System.currentTimeMillis()}" })
            
            usersCollection.document(userWithId.id).set(userWithId).await()
            
            Log.d("UserService", "Usuario creado exitosamente en Firestore: ${userWithId.id}")
            Result.success(userWithId)
            
        } catch (e: Exception) {
            Log.e("UserService", "Error al crear usuario en Firestore: ${e.message}")
            Result.failure(e)
        }
    }
    
    suspend fun getUserById(userId: String): Result<User?> {
        return try {
            Log.d("UserService", "Buscando usuario por ID: $userId")
            val document = usersCollection.document(userId).get().await()
            
            if (document.exists()) {
                val user = document.toObject(User::class.java)
                Log.d("UserService", "Usuario encontrado: ${user?.email}")
                Result.success(user)
            } else {
                Log.d("UserService", "Usuario no encontrado: $userId")
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e("UserService", "Error al buscar usuario: ${e.message}")
            Result.failure(e)
        }
    }
    
    suspend fun getUserByEmail(email: String): Result<User?> {
        return try {
            Log.d("UserService", "Buscando usuario por email: $email")
            val query = usersCollection.whereEqualTo("email", email).get().await()
            
            if (!query.isEmpty) {
                val user = query.documents.first().toObject(User::class.java)
                Log.d("UserService", "Usuario encontrado por email: ${user?.email}")
                Result.success(user)
            } else {
                Log.d("UserService", "Usuario no encontrado por email: $email")
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e("UserService", "Error al buscar usuario por email: ${e.message}")
            Result.failure(e)
        }
    }
    
    suspend fun getUserByUsername(username: String): Result<User?> {
        return try {
            Log.d("UserService", "Buscando usuario por username: $username")
            val query = usersCollection.whereEqualTo("username", username).get().await()
            
            if (!query.isEmpty) {
                val user = query.documents.first().toObject(User::class.java)
                Log.d("UserService", "Usuario encontrado por username: ${user?.username}")
                Result.success(user)
            } else {
                Log.d("UserService", "Usuario no encontrado por username: $username")
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e("UserService", "Error al buscar usuario por username: ${e.message}")
            Result.failure(e)
        }
    }
    
    private suspend fun checkUsernameExists(username: String): Boolean {
        return try {
            val query = usersCollection.whereEqualTo("username", username).get().await()
            !query.isEmpty
        } catch (e: Exception) {
            Log.e("UserService", "Error al verificar username: ${e.message}")
            false
        }
    }
    
    private suspend fun checkEmailExists(email: String): Boolean {
        return try {
            val query = usersCollection.whereEqualTo("email", email).get().await()
            !query.isEmpty
        } catch (e: Exception) {
            Log.e("UserService", "Error al verificar email: ${e.message}")
            false
        }
    }
    
    suspend fun updateUser(user: User): Result<User> {
        return try {
            Log.d("UserService", "Actualizando usuario: ${user.id}")
            usersCollection.document(user.id).set(user).await()
            Log.d("UserService", "Usuario actualizado exitosamente")
            Result.success(user)
        } catch (e: Exception) {
            Log.e("UserService", "Error al actualizar usuario: ${e.message}")
            Result.failure(e)
        }
    }
    
    suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            Log.d("UserService", "Eliminando usuario: $userId")
            usersCollection.document(userId).delete().await()
            Log.d("UserService", "Usuario eliminado exitosamente")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserService", "Error al eliminar usuario: ${e.message}")
            Result.failure(e)
        }
    }
}

