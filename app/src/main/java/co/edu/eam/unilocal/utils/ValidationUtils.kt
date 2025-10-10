package co.edu.eam.unilocal.utils

import android.util.Patterns
import java.util.regex.Pattern

/**
 * Utilidades de validación para formularios
 */
object ValidationUtils {
    
    // Patrones de validación
    private val EMAIL_PATTERN = Patterns.EMAIL_ADDRESS
    private val USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$")
    private val NAME_PATTERN = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$")
    private val CITY_PATTERN = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$")
    private val PHONE_PATTERN = Pattern.compile("^[+]?[0-9\\s\\-()]{7,15}$")
    
    // Resultado de validación
    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )
    
    /**
     * Valida un email
     */
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult(false, "El email es obligatorio")
            !EMAIL_PATTERN.matcher(email).matches() -> ValidationResult(false, "Ingresa un email válido")
            email.length > 100 -> ValidationResult(false, "El email es demasiado largo")
            else -> ValidationResult(true)
        }
    }
    
    /**
     * Valida una contraseña
     */
    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult(false, "La contraseña es obligatoria")
            password.length < 6 -> ValidationResult(false, "La contraseña debe tener al menos 6 caracteres")
            password.length > 50 -> ValidationResult(false, "La contraseña es demasiado larga")
            !password.any { it.isDigit() } -> ValidationResult(false, "La contraseña debe contener al menos un número")
            !password.any { it.isLetter() } -> ValidationResult(false, "La contraseña debe contener al menos una letra")
            else -> ValidationResult(true)
        }
    }
    
    /**
     * Valida confirmación de contraseña
     */
    fun validatePasswordConfirmation(password: String, confirmPassword: String): ValidationResult {
        return when {
            confirmPassword.isBlank() -> ValidationResult(false, "Confirma tu contraseña")
            password != confirmPassword -> ValidationResult(false, "Las contraseñas no coinciden")
            else -> ValidationResult(true)
        }
    }
    
    /**
     * Valida un nombre (primer nombre o apellido)
     */
    fun validateName(name: String, fieldName: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult(false, "El $fieldName es obligatorio")
            !NAME_PATTERN.matcher(name.trim()).matches() -> ValidationResult(false, "El $fieldName solo puede contener letras y espacios")
            name.trim().length < 2 -> ValidationResult(false, "El $fieldName debe tener al menos 2 caracteres")
            name.trim().length > 50 -> ValidationResult(false, "El $fieldName es demasiado largo")
            else -> ValidationResult(true)
        }
    }
    
    /**
     * Valida un nombre de usuario
     */
    fun validateUsername(username: String): ValidationResult {
        return when {
            username.isBlank() -> ValidationResult(false, "El nombre de usuario es obligatorio")
            !USERNAME_PATTERN.matcher(username).matches() -> ValidationResult(false, "El nombre de usuario solo puede contener letras, números y guiones bajos (3-20 caracteres)")
            username.startsWith("_") || username.endsWith("_") -> ValidationResult(false, "El nombre de usuario no puede empezar o terminar con guión bajo")
            else -> ValidationResult(true)
        }
    }
    
    /**
     * Valida una ciudad
     */
    fun validateCity(city: String): ValidationResult {
        return when {
            city.isBlank() -> ValidationResult(false, "La ciudad es obligatoria")
            !CITY_PATTERN.matcher(city.trim()).matches() -> ValidationResult(false, "La ciudad solo puede contener letras y espacios")
            city.trim().length < 2 -> ValidationResult(false, "La ciudad debe tener al menos 2 caracteres")
            city.trim().length > 50 -> ValidationResult(false, "La ciudad es demasiado larga")
            else -> ValidationResult(true)
        }
    }
    
    /**
     * Valida un teléfono
     */
    fun validatePhone(phone: String): ValidationResult {
        return when {
            phone.isBlank() -> ValidationResult(true) // Teléfono es opcional
            !PHONE_PATTERN.matcher(phone).matches() -> ValidationResult(false, "Ingresa un número de teléfono válido")
            else -> ValidationResult(true)
        }
    }
    
    /**
     * Valida un nombre de lugar
     */
    fun validatePlaceName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult(false, "El nombre del lugar es obligatorio")
            name.trim().length < 3 -> ValidationResult(false, "El nombre del lugar debe tener al menos 3 caracteres")
            name.trim().length > 100 -> ValidationResult(false, "El nombre del lugar es demasiado largo")
            else -> ValidationResult(true)
        }
    }
    
    /**
     * Valida una descripción
     */
    fun validateDescription(description: String): ValidationResult {
        return when {
            description.isBlank() -> ValidationResult(false, "La descripción es obligatoria")
            description.trim().length < 10 -> ValidationResult(false, "La descripción debe tener al menos 10 caracteres")
            description.trim().length > 500 -> ValidationResult(false, "La descripción es demasiado larga")
            else -> ValidationResult(true)
        }
    }
    
    /**
     * Valida una dirección
     */
    fun validateAddress(address: String): ValidationResult {
        return when {
            address.isBlank() -> ValidationResult(false, "La dirección es obligatoria")
            address.trim().length < 5 -> ValidationResult(false, "La dirección debe tener al menos 5 caracteres")
            address.trim().length > 200 -> ValidationResult(false, "La dirección es demasiado larga")
            else -> ValidationResult(true)
        }
    }
    
    /**
     * Valida un formulario completo de registro
     */
    fun validateRegistrationForm(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        city: String,
        password: String,
        confirmPassword: String
    ): ValidationResult {
        // Validar cada campo individualmente
        val firstNameResult = validateName(firstName, "nombre")
        if (!firstNameResult.isValid) return firstNameResult
        
        val lastNameResult = validateName(lastName, "apellido")
        if (!lastNameResult.isValid) return lastNameResult
        
        val usernameResult = validateUsername(username)
        if (!usernameResult.isValid) return usernameResult
        
        val emailResult = validateEmail(email)
        if (!emailResult.isValid) return emailResult
        
        val cityResult = validateCity(city)
        if (!cityResult.isValid) return cityResult
        
        val passwordResult = validatePassword(password)
        if (!passwordResult.isValid) return passwordResult
        
        val confirmPasswordResult = validatePasswordConfirmation(password, confirmPassword)
        if (!confirmPasswordResult.isValid) return confirmPasswordResult
        
        return ValidationResult(true)
    }
    
    /**
     * Valida un formulario de login
     */
    fun validateLoginForm(email: String, password: String): ValidationResult {
        val emailResult = validateEmail(email)
        if (!emailResult.isValid) return emailResult
        
        return when {
            password.isBlank() -> ValidationResult(false, "La contraseña es obligatoria")
            else -> ValidationResult(true)
        }
    }
    
    /**
     * Sanitiza un texto removiendo espacios extra y caracteres especiales
     */
    fun sanitizeText(text: String): String {
        return text.trim().replace(Regex("\\s+"), " ")
    }
    
    /**
     * Formatea un nombre de usuario para que sea válido
     */
    fun formatUsername(username: String): String {
        return username.lowercase()
            .replace(Regex("[^a-z0-9_]"), "")
            .take(20)
    }
    
    /**
     * Formatea un email
     */
    fun formatEmail(email: String): String {
        return email.trim().lowercase()
    }
}
