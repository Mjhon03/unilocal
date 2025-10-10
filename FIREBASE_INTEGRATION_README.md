# Integración de Firebase - uniLocal

## Descripción
Se ha implementado un sistema completo de autenticación y gestión de usuarios con Firebase para la aplicación uniLocal.

## Características Implementadas

### 1. **Modelo de Datos**
- **User**: Modelo completo de usuario con roles (USER, MODERATOR, ADMIN)
- **UserRole**: Enum para diferentes tipos de usuarios
- Soporte para serialización con Kotlinx Serialization

### 2. **Servicios de Firebase**

#### **AuthService**
- Registro de usuarios con Firebase Authentication
- Inicio de sesión con email y contraseña
- Cierre de sesión
- Verificación de email
- Restablecimiento de contraseña
- Eliminación de cuenta

#### **UserService**
- Creación de usuarios en Firestore
- Búsqueda por ID, email o username
- Actualización de datos de usuario
- Eliminación de usuarios
- Verificación de disponibilidad de username/email
- Gestión de roles de usuario

### 3. **ViewModel**
- **AuthViewModel**: Maneja el estado de autenticación
- Estados reactivos con StateFlow
- Integración completa entre AuthService y UserService
- Validaciones de datos

### 4. **Inyección de Dependencias**
- **Hilt**: Configurado para inyección de dependencias
- **FirebaseModule**: Módulo para proveer servicios
- **UniLocalApplication**: Application class con Hilt

## Configuración del Proyecto

### **Dependencias Agregadas**
```kotlin
// Firebase BoM
implementation(platform("com.google.firebase:firebase-bom:34.3.0"))

// Firebase Services
implementation("com.google.firebase:firebase-analytics")
implementation("com.google.firebase:firebase-auth")
implementation("com.google.firebase:firebase-firestore")
implementation("com.google.firebase:firebase-storage")
implementation("com.google.firebase:firebase-messaging")

// Hilt
implementation("com.google.dagger:hilt-android:2.48")
implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
kapt("com.google.dagger:hilt-compiler:2.48")
```

### **Plugins Configurados**
- `com.google.gms.google-services`
- `com.google.dagger.hilt.android`
- `kotlin("kapt")`

## Estructura de Datos en Firestore

### **Colección: users**
```json
{
  "id": "firebase_uid",
  "email": "usuario@email.com",
  "firstName": "Juan",
  "lastName": "Pérez",
  "username": "juanperez",
  "city": "Bogotá",
  "profileImageUrl": "https://...",
  "createdAt": 1640995200000,
  "isActive": true,
  "role": "USER"
}
```

## Cómo Usar

### **1. Inyectar AuthViewModel en una pantalla**
```kotlin
@Composable
fun MyScreen(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val isLoading by authViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by authViewModel.errorMessage.collectAsStateWithLifecycle()
    
    // Usar los estados...
}
```

### **2. Registrar un nuevo usuario**
```kotlin
authViewModel.registerUser(
    email = "usuario@email.com",
    password = "contraseña123",
    firstName = "Juan",
    lastName = "Pérez",
    username = "juanperez",
    city = "Bogotá"
)
```

### **3. Iniciar sesión**
```kotlin
authViewModel.signInUser(
    email = "usuario@email.com",
    password = "contraseña123"
)
```

### **4. Cerrar sesión**
```kotlin
authViewModel.signOut()
```

### **5. Verificar roles de usuario**
```kotlin
val isModerator = authViewModel.isUserModerator()
val isAdmin = authViewModel.isUserAdmin()
```

## Estados de Autenticación

### **AuthState**
- `Loading`: Verificando estado de autenticación
- `Authenticated`: Usuario autenticado
- `Unauthenticated`: Usuario no autenticado

### **Estados del ViewModel**
- `authState`: Estado actual de autenticación
- `currentUser`: Datos del usuario actual
- `isLoading`: Indicador de carga
- `errorMessage`: Mensajes de error

## Flujo de Registro

1. **Validación**: Verificar que username y email no existan
2. **Firebase Auth**: Crear usuario en Firebase Authentication
3. **Firestore**: Guardar datos adicionales en Firestore
4. **Estado**: Actualizar estado de autenticación

## Flujo de Inicio de Sesión

1. **Firebase Auth**: Autenticar con email y contraseña
2. **Firestore**: Cargar datos del usuario desde Firestore
3. **Estado**: Actualizar estado de autenticación

## Seguridad

### **Reglas de Firestore (Recomendadas)**
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Usuarios solo pueden leer/escribir sus propios datos
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Solo administradores pueden leer todos los usuarios
    match /users/{document=**} {
      allow read: if request.auth != null && 
        get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'ADMIN';
    }
  }
}
```

## Próximos Pasos

1. **Configurar reglas de Firestore** para seguridad
2. **Implementar verificación de email** obligatoria
3. **Agregar autenticación con Google/Facebook**
4. **Implementar recuperación de contraseña**
5. **Agregar validaciones de datos** más robustas
6. **Implementar notificaciones push** para moderadores

## Archivos Creados/Modificados

### **Nuevos Archivos:**
- `models/User.kt`
- `services/AuthService.kt`
- `services/UserService.kt`
- `viewmodels/AuthViewModel.kt`
- `di/FirebaseModule.kt`
- `UniLocalApplication.kt`
- `ui/screens/AuthExampleScreen.kt`

### **Archivos Modificados:**
- `build.gradle.kts` (proyecto y app)
- `gradle/libs.versions.toml`
- `AndroidManifest.xml`
- `MainActivity.kt`

## Testing

Para probar la integración:

1. **Ejecutar la app** y navegar a `AuthExampleScreen`
2. **Registrar un usuario** con datos válidos
3. **Verificar en Firebase Console** que el usuario aparezca en Authentication y Firestore
4. **Iniciar sesión** con las credenciales creadas
5. **Verificar** que el estado de autenticación se actualice correctamente

## Troubleshooting

### **Errores Comunes:**
- **"Plugin not found"**: Verificar que todos los plugins estén agregados
- **"Hilt not initialized"**: Verificar que `@HiltAndroidApp` esté en Application class
- **"Firebase not initialized"**: Verificar que `google-services.json` esté en la carpeta `app/`
- **"Permission denied"**: Configurar reglas de Firestore correctamente

### **Logs Útiles:**
```kotlin
// En AuthService y UserService
Log.d("Firebase", "Operación exitosa: $result")
Log.e("Firebase", "Error: ${exception.message}")
```
