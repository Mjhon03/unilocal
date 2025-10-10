package co.edu.eam.unilocal.utils

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

/**
 * Estado de validación para campos de formulario
 */
data class FieldValidationState(
    val value: String = "",
    val error: String? = null,
    val isValid: Boolean = false,
    val isTouched: Boolean = false
)

/**
 * Estado de validación para formularios completos
 */
class FormValidationState {
    
    // Estados de campos individuales
    var firstName by mutableStateOf(FieldValidationState())
        private set
    
    var lastName by mutableStateOf(FieldValidationState())
        private set
    
    var username by mutableStateOf(FieldValidationState())
        private set
    
    var email by mutableStateOf(FieldValidationState())
        private set
    
    var city by mutableStateOf(FieldValidationState())
        private set
    
    var password by mutableStateOf(FieldValidationState())
        private set
    
    var confirmPassword by mutableStateOf(FieldValidationState())
        private set
    
    // Estado general del formulario
    var isFormValid by mutableStateOf(false)
        private set
    
    var generalError by mutableStateOf<String?>(null)
        private set
    
    /**
     * Actualiza el valor de un campo y valida
     */
    fun updateField(field: FormField, value: String) {
        val sanitizedValue = ValidationUtils.sanitizeText(value)
        
        when (field) {
            FormField.FIRST_NAME -> {
                val result = ValidationUtils.validateName(sanitizedValue, "nombre")
                firstName = FieldValidationState(
                    value = sanitizedValue,
                    error = if (firstName.isTouched) result.errorMessage else null,
                    isValid = result.isValid,
                    isTouched = firstName.isTouched
                )
            }
            FormField.LAST_NAME -> {
                val result = ValidationUtils.validateName(sanitizedValue, "apellido")
                lastName = FieldValidationState(
                    value = sanitizedValue,
                    error = if (lastName.isTouched) result.errorMessage else null,
                    isValid = result.isValid,
                    isTouched = lastName.isTouched
                )
            }
            FormField.USERNAME -> {
                val formattedValue = ValidationUtils.formatUsername(sanitizedValue)
                val result = ValidationUtils.validateUsername(formattedValue)
                username = FieldValidationState(
                    value = formattedValue,
                    error = if (username.isTouched) result.errorMessage else null,
                    isValid = result.isValid,
                    isTouched = username.isTouched
                )
            }
            FormField.EMAIL -> {
                val formattedValue = ValidationUtils.formatEmail(sanitizedValue)
                val result = ValidationUtils.validateEmail(formattedValue)
                email = FieldValidationState(
                    value = formattedValue,
                    error = if (email.isTouched) result.errorMessage else null,
                    isValid = result.isValid,
                    isTouched = email.isTouched
                )
            }
            FormField.CITY -> {
                val result = ValidationUtils.validateCity(sanitizedValue)
                city = FieldValidationState(
                    value = sanitizedValue,
                    error = if (city.isTouched) result.errorMessage else null,
                    isValid = result.isValid,
                    isTouched = city.isTouched
                )
            }
            FormField.PASSWORD -> {
                val result = ValidationUtils.validatePassword(sanitizedValue)
                password = FieldValidationState(
                    value = sanitizedValue,
                    error = if (password.isTouched) result.errorMessage else null,
                    isValid = result.isValid,
                    isTouched = password.isTouched
                )
                // Revalidar confirmación de contraseña si ya fue tocada
                if (confirmPassword.isTouched) {
                    val confirmResult = ValidationUtils.validatePasswordConfirmation(sanitizedValue, confirmPassword.value)
                    confirmPassword = confirmPassword.copy(
                        error = confirmResult.errorMessage,
                        isValid = confirmResult.isValid
                    )
                }
            }
            FormField.CONFIRM_PASSWORD -> {
                val result = ValidationUtils.validatePasswordConfirmation(password.value, sanitizedValue)
                confirmPassword = FieldValidationState(
                    value = sanitizedValue,
                    error = if (confirmPassword.isTouched) result.errorMessage else null,
                    isValid = result.isValid,
                    isTouched = confirmPassword.isTouched
                )
            }
        }
        
        updateFormValidity()
    }
    
    /**
     * Marca un campo como tocado (para mostrar errores)
     */
    fun touchField(field: FormField) {
        when (field) {
            FormField.FIRST_NAME -> {
                firstName = firstName.copy(isTouched = true)
                if (!firstName.isValid) {
                    val result = ValidationUtils.validateName(firstName.value, "nombre")
                    firstName = firstName.copy(error = result.errorMessage)
                }
            }
            FormField.LAST_NAME -> {
                lastName = lastName.copy(isTouched = true)
                if (!lastName.isValid) {
                    val result = ValidationUtils.validateName(lastName.value, "apellido")
                    lastName = lastName.copy(error = result.errorMessage)
                }
            }
            FormField.USERNAME -> {
                username = username.copy(isTouched = true)
                if (!username.isValid) {
                    val result = ValidationUtils.validateUsername(username.value)
                    username = username.copy(error = result.errorMessage)
                }
            }
            FormField.EMAIL -> {
                email = email.copy(isTouched = true)
                if (!email.isValid) {
                    val result = ValidationUtils.validateEmail(email.value)
                    email = email.copy(error = result.errorMessage)
                }
            }
            FormField.CITY -> {
                city = city.copy(isTouched = true)
                if (!city.isValid) {
                    val result = ValidationUtils.validateCity(city.value)
                    city = city.copy(error = result.errorMessage)
                }
            }
            FormField.PASSWORD -> {
                password = password.copy(isTouched = true)
                if (!password.isValid) {
                    val result = ValidationUtils.validatePassword(password.value)
                    password = password.copy(error = result.errorMessage)
                }
            }
            FormField.CONFIRM_PASSWORD -> {
                confirmPassword = confirmPassword.copy(isTouched = true)
                if (!confirmPassword.isValid) {
                    val result = ValidationUtils.validatePasswordConfirmation(password.value, confirmPassword.value)
                    confirmPassword = confirmPassword.copy(error = result.errorMessage)
                }
            }
        }
        
        updateFormValidity()
    }
    
    /**
     * Valida todo el formulario
     */
    fun validateForm(): ValidationUtils.ValidationResult {
        val result = ValidationUtils.validateRegistrationForm(
            firstName = firstName.value,
            lastName = lastName.value,
            username = username.value,
            email = email.value,
            city = city.value,
            password = password.value,
            confirmPassword = confirmPassword.value
        )
        
        generalError = result.errorMessage
        return result
    }
    
    /**
     * Limpia todos los errores
     */
    fun clearErrors() {
        generalError = null
        firstName = firstName.copy(error = null)
        lastName = lastName.copy(error = null)
        username = username.copy(error = null)
        email = email.copy(error = null)
        city = city.copy(error = null)
        password = password.copy(error = null)
        confirmPassword = confirmPassword.copy(error = null)
    }
    
    /**
     * Resetea todo el formulario
     */
    fun resetForm() {
        firstName = FieldValidationState()
        lastName = FieldValidationState()
        username = FieldValidationState()
        email = FieldValidationState()
        city = FieldValidationState()
        password = FieldValidationState()
        confirmPassword = FieldValidationState()
        isFormValid = false
        generalError = null
    }
    
    /**
     * Actualiza la validez general del formulario
     */
    private fun updateFormValidity() {
        isFormValid = firstName.isValid &&
                lastName.isValid &&
                username.isValid &&
                email.isValid &&
                city.isValid &&
                password.isValid &&
                confirmPassword.isValid
    }
}

/**
 * Campos del formulario
 */
enum class FormField {
    FIRST_NAME,
    LAST_NAME,
    USERNAME,
    EMAIL,
    CITY,
    PASSWORD,
    CONFIRM_PASSWORD
}

/**
 * Estado de validación para login
 */
class LoginValidationState {
    
    var email by mutableStateOf(FieldValidationState())
        private set
    
    var password by mutableStateOf(FieldValidationState())
        private set
    
    var isFormValid by mutableStateOf(false)
        private set
    
    var generalError by mutableStateOf<String?>(null)
        private set
    
    fun updateEmail(value: String) {
        val formattedValue = ValidationUtils.formatEmail(value)
        val result = ValidationUtils.validateEmail(formattedValue)
        email = FieldValidationState(
            value = formattedValue,
            error = if (email.isTouched) result.errorMessage else null,
            isValid = result.isValid,
            isTouched = email.isTouched
        )
        updateFormValidity()
    }
    
    fun updatePassword(value: String) {
        password = FieldValidationState(
            value = value,
            error = if (password.isTouched && value.isBlank()) "La contraseña es obligatoria" else null,
            isValid = value.isNotBlank(),
            isTouched = password.isTouched
        )
        updateFormValidity()
    }
    
    fun touchEmail() {
        email = email.copy(isTouched = true)
        if (!email.isValid) {
            val result = ValidationUtils.validateEmail(email.value)
            email = email.copy(error = result.errorMessage)
        }
        updateFormValidity()
    }
    
    fun touchPassword() {
        password = password.copy(isTouched = true)
        if (!password.isValid) {
            password = password.copy(error = "La contraseña es obligatoria")
        }
        updateFormValidity()
    }
    
    fun validateForm(): ValidationUtils.ValidationResult {
        val result = ValidationUtils.validateLoginForm(email.value, password.value)
        generalError = result.errorMessage
        return result
    }
    
    fun clearErrors() {
        generalError = null
        email = email.copy(error = null)
        password = password.copy(error = null)
    }
    
    fun resetForm() {
        email = FieldValidationState()
        password = FieldValidationState()
        isFormValid = false
        generalError = null
    }
    
    private fun updateFormValidity() {
        isFormValid = email.isValid && password.isValid
    }
}
