package co.edu.eam.unilocal.utils

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

/**
 * Estado de validación para el formulario de crear lugar
 */
class PlaceValidationState {
    
    // Estados de campos individuales
    private var _placeName by mutableStateOf(FieldValidationState())
    val placeName: State<FieldValidationState> = mutableStateOf(_placeName)
    
    private var _description by mutableStateOf(FieldValidationState())
    val description: State<FieldValidationState> = mutableStateOf(_description)
    
    private var _address by mutableStateOf(FieldValidationState())
    val address: State<FieldValidationState> = mutableStateOf(_address)
    
    private var _phone by mutableStateOf(FieldValidationState())
    val phone: State<FieldValidationState> = mutableStateOf(_phone)
    
    // Estado general del formulario
    private var _isFormValid by mutableStateOf(false)
    val isFormValid: State<Boolean> = mutableStateOf(_isFormValid)
    
    private var _generalError by mutableStateOf<String?>(null)
    val generalError: State<String?> = mutableStateOf(_generalError)
    
    /**
     * Actualiza el valor de un campo y valida
     */
    fun updateField(field: PlaceField, value: String) {
        val sanitizedValue = ValidationUtils.sanitizeText(value)
        
        when (field) {
            PlaceField.PLACE_NAME -> {
                val result = ValidationUtils.validatePlaceName(sanitizedValue)
                _placeName = FieldValidationState(
                    value = sanitizedValue,
                    error = if (_placeName.isTouched) result.errorMessage else null,
                    isValid = result.isValid,
                    isTouched = _placeName.isTouched
                )
            }
            PlaceField.DESCRIPTION -> {
                val result = ValidationUtils.validateDescription(sanitizedValue)
                _description = FieldValidationState(
                    value = sanitizedValue,
                    error = if (_description.isTouched) result.errorMessage else null,
                    isValid = result.isValid,
                    isTouched = _description.isTouched
                )
            }
            PlaceField.ADDRESS -> {
                val result = ValidationUtils.validateAddress(sanitizedValue)
                _address = FieldValidationState(
                    value = sanitizedValue,
                    error = if (_address.isTouched) result.errorMessage else null,
                    isValid = result.isValid,
                    isTouched = _address.isTouched
                )
            }
            PlaceField.PHONE -> {
                val result = ValidationUtils.validatePhone(sanitizedValue)
                _phone = FieldValidationState(
                    value = sanitizedValue,
                    error = if (_phone.isTouched) result.errorMessage else null,
                    isValid = result.isValid,
                    isTouched = _phone.isTouched
                )
            }
        }
        
        updateFormValidity()
    }
    
    /**
     * Marca un campo como tocado (para mostrar errores)
     */
    fun touchField(field: PlaceField) {
        when (field) {
            PlaceField.PLACE_NAME -> {
                _placeName = _placeName.copy(isTouched = true)
                if (!_placeName.isValid) {
                    val result = ValidationUtils.validatePlaceName(_placeName.value)
                    _placeName = _placeName.copy(error = result.errorMessage)
                }
            }
            PlaceField.DESCRIPTION -> {
                _description = _description.copy(isTouched = true)
                if (!_description.isValid) {
                    val result = ValidationUtils.validateDescription(_description.value)
                    _description = _description.copy(error = result.errorMessage)
                }
            }
            PlaceField.ADDRESS -> {
                _address = _address.copy(isTouched = true)
                if (!_address.isValid) {
                    val result = ValidationUtils.validateAddress(_address.value)
                    _address = _address.copy(error = result.errorMessage)
                }
            }
            PlaceField.PHONE -> {
                _phone = _phone.copy(isTouched = true)
                if (!_phone.isValid) {
                    val result = ValidationUtils.validatePhone(_phone.value)
                    _phone = _phone.copy(error = result.errorMessage)
                }
            }
        }
        
        updateFormValidity()
    }
    
    /**
     * Valida todo el formulario
     */
    fun validateForm(): ValidationUtils.ValidationResult {
        val placeNameResult = ValidationUtils.validatePlaceName(_placeName.value)
        val descriptionResult = ValidationUtils.validateDescription(_description.value)
        val addressResult = ValidationUtils.validateAddress(_address.value)
        val phoneResult = ValidationUtils.validatePhone(_phone.value)
        
        val firstError = listOf(
            placeNameResult.errorMessage,
            descriptionResult.errorMessage,
            addressResult.errorMessage,
            phoneResult.errorMessage
        ).firstOrNull { it != null }
        
        val isValid = placeNameResult.isValid && 
                descriptionResult.isValid && 
                addressResult.isValid && 
                phoneResult.isValid
        
        _generalError = firstError
        return ValidationUtils.ValidationResult(isValid, firstError)
    }
    
    /**
     * Limpia todos los errores
     */
    fun clearErrors() {
        _generalError = null
        _placeName = _placeName.copy(error = null)
        _description = _description.copy(error = null)
        _address = _address.copy(error = null)
        _phone = _phone.copy(error = null)
    }
    
    /**
     * Resetea todo el formulario
     */
    fun resetForm() {
        _placeName = FieldValidationState()
        _description = FieldValidationState()
        _address = FieldValidationState()
        _phone = FieldValidationState()
        _isFormValid = false
        _generalError = null
    }
    
    /**
     * Actualiza la validez general del formulario
     */
    private fun updateFormValidity() {
        _isFormValid = _placeName.isValid &&
                _description.isValid &&
                _address.isValid &&
                _phone.isValid
    }
}

/**
 * Campos del formulario de lugar
 */
enum class PlaceField {
    PLACE_NAME,
    DESCRIPTION,
    ADDRESS,
    PHONE
}
