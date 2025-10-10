# Sistema de Validaciones - UniLocal

Este documento explica cómo usar el sistema de validaciones implementado en la aplicación UniLocal.

## Archivos Principales

### 1. ValidationUtils.kt
Contiene todas las funciones de validación reutilizables:
- `validateEmail()` - Valida formato de email
- `validatePassword()` - Valida contraseñas (mínimo 6 caracteres, debe contener letras y números)
- `validateName()` - Valida nombres y apellidos
- `validateUsername()` - Valida nombres de usuario (3-20 caracteres, solo letras, números y guiones bajos)
- `validateCity()` - Valida nombres de ciudades
- `validatePhone()` - Valida números de teléfono (opcional)
- `validatePlaceName()` - Valida nombres de lugares
- `validateDescription()` - Valida descripciones (mínimo 10 caracteres)
- `validateAddress()` - Valida direcciones

### 2. FormValidationState.kt
Maneja el estado de validación para formularios de registro y login:
- `FormValidationState` - Para formularios de registro
- `LoginValidationState` - Para formularios de login
- `FieldValidationState` - Estado individual de cada campo

### 3. PlaceValidationState.kt
Maneja el estado de validación para formularios de lugares:
- `PlaceValidationState` - Para formularios de crear/editar lugares

## Cómo Usar las Validaciones

### 1. En un Composable

```kotlin
@Composable
fun MyForm() {
    val validationState = remember { FormValidationState() }
    
    Column {
        // Campo de email
        OutlinedTextField(
            value = validationState.email.value.value,
            onValueChange = { validationState.updateField(FormField.EMAIL, it) },
            onFocusChange = { focused ->
                if (!focused) {
                    validationState.touchField(FormField.EMAIL)
                }
            },
            isError = validationState.email.value.error != null,
            supportingText = validationState.email.value.error?.let { error ->
                { Text(text = error, color = MaterialTheme.colorScheme.error) }
            }
        )
        
        // Botón de envío
        Button(
            onClick = {
                if (validationState.validateForm().isValid) {
                    // Proceder con el envío
                }
            },
            enabled = validationState.isFormValid.value
        ) {
            Text("Enviar")
        }
    }
}
```

### 2. Validación en Tiempo Real

Las validaciones se ejecutan automáticamente cuando:
- El usuario escribe en un campo
- El usuario sale del campo (onFocusChange)
- Se llama manualmente a `validateForm()`

### 3. Estados de Validación

Cada campo tiene los siguientes estados:
- `value` - El valor actual del campo
- `error` - Mensaje de error (null si no hay error)
- `isValid` - Si el campo es válido
- `isTouched` - Si el usuario ha interactuado con el campo

### 4. Manejo de Errores

```kotlin
// Mostrar error general
validationState.generalError.value?.let { error ->
    Text(
        text = error,
        color = MaterialTheme.colorScheme.error
    )
}

// Limpiar errores
validationState.clearErrors()

// Resetear formulario
validationState.resetForm()
```

## Características del Sistema

### ✅ Validaciones Implementadas
- **Email**: Formato válido, longitud máxima
- **Contraseña**: Mínimo 6 caracteres, debe contener letras y números
- **Nombres**: Solo letras y espacios, longitud 2-50 caracteres
- **Usuario**: Letras, números y guiones bajos, 3-20 caracteres
- **Ciudad**: Solo letras y espacios, longitud 2-50 caracteres
- **Teléfono**: Formato válido (opcional)
- **Lugar**: Longitud 3-100 caracteres
- **Descripción**: Longitud 10-500 caracteres
- **Dirección**: Longitud 5-200 caracteres

### ✅ Funcionalidades
- Validación en tiempo real
- Mensajes de error específicos
- Sanitización automática de texto
- Formateo automático (emails en minúsculas, usuarios sin caracteres especiales)
- Estados de formulario (válido/inválido)
- Scroll automático en formularios largos
- Ocultación automática del teclado

### ✅ UX Mejorada
- Los errores solo se muestran después de que el usuario interactúe con el campo
- Los errores se limpian automáticamente cuando el usuario corrige el campo
- El botón de envío se deshabilita hasta que el formulario sea válido
- Indicadores visuales claros para campos con errores
- Mensajes de error en español y específicos

## Ejemplos de Uso

### Formulario de Login
```kotlin
val loginState = remember { LoginValidationState() }
```

### Formulario de Registro
```kotlin
val registerState = remember { FormValidationState() }
```

### Formulario de Lugar
```kotlin
val placeState = remember { PlaceValidationState() }
```

## Personalización

Para agregar nuevas validaciones:

1. Agregar la función de validación en `ValidationUtils.kt`
2. Agregar el campo en el enum correspondiente (`FormField`, `PlaceField`, etc.)
3. Actualizar el estado de validación correspondiente
4. Agregar los strings de error en `strings.xml`

## Strings de Validación

Todos los mensajes de error están en `strings.xml` bajo la sección "Validation Messages" y "Field Names" para facilitar la localización.
