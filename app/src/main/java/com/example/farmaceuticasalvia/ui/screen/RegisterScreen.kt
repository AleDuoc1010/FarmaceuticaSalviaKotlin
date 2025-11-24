package com.example.farmaceuticasalvia.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.farmaceuticasalvia.ui.viewmodel.AuthViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.farmaceuticasalvia.ui.theme.Beige

@Composable
fun RegisterScreenVm(
    vm: AuthViewModel,
    onRegisterNavigateLogin: () -> Unit,
    onGoLogin: () -> Unit
){
    val state by vm.register.collectAsStateWithLifecycle()

    LaunchedEffect(state.success) {
        if (state.success) {
            onRegisterNavigateLogin()
            vm.clearRegisterResult()
        }
    }

    RegisterScreen(
        name = state.name,                                   // 1) Nombre
        email = state.email,                                 // 2) Email
        phone = state.phone,                                 // 3) Teléfono
        pass = state.pass,                                   // 4) Password
        confirm = state.confirm,                             // 5) Confirmación

        nameError = state.nameError,                         // Errores por campo
        emailError = state.emailError,
        phoneError = state.phoneError,
        passError = state.passError,
        confirmError = state.confirmError,

        canSubmit = state.canSubmit,                         // Habilitar "Registrar"
        isSubmitting = state.isSubmitting,                   // Flag de carga
        errorMsg = state.errorMsg,                           // Error global (duplicado)

        onNameChange = vm::onNameChange,                     // Handlers
        onEmailChange = vm::onRegisterEmailChange,
        onPhoneChange = vm::onRegisterPhoneChange,
        onPassChange = vm::onRegisterPassChange,
        onConfirmChange = vm::onConfirmChange,

        onSubmit = vm::submitRegister,                       // Acción Registrar
        onGoLogin = onGoLogin
    )
}
@Composable
private fun RegisterScreen(
    name: String,                                            // 1) Nombre (solo letras/espacios)
    email: String,                                           // 2) Email
    phone: String,                                           // 3) Teléfono (solo números)
    pass: String,                                            // 4) Password (segura)
    confirm: String,                                         // 5) Confirmación
    nameError: String?,                                      // Errores
    emailError: String?,
    phoneError: String?,
    passError: String?,
    confirmError: String?,
    canSubmit: Boolean,                                      // Habilitar botón
    isSubmitting: Boolean,                                   // Flag de carga
    errorMsg: String?,                                       // Error global (duplicado)
    onNameChange: (String) -> Unit,                          // Handler nombre
    onEmailChange: (String) -> Unit,                         // Handler email
    onPhoneChange: (String) -> Unit,                         // Handler teléfono
    onPassChange: (String) -> Unit,                          // Handler password
    onConfirmChange: (String) -> Unit,                       // Handler confirmación
    onSubmit: () -> Unit,                                    // Acción Registrar
    onGoLogin: () -> Unit
){
    var showPass by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Beige)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ){
        Column(modifier = Modifier.fillMaxWidth()){
            Text(
                text = "Registro",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Nombre")},
                singleLine = true,
                isError = nameError != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if(nameError != null){
                Text(nameError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = {Text("Email")},
                singleLine = true,
                isError = emailError != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (emailError != null) {
                Text(emailError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(8.dp))


            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = {Text("Telefono")},
                singleLine = true,
                isError = phoneError != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (phoneError != null){
                Text(phoneError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = pass,
                onValueChange = onPassChange,
                label = {Text("Contraseña")},
                singleLine = true,
                isError = passError != null,
                visualTransformation = if(showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = {showPass = !showPass}) {
                        Icon(
                            imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPass) "Ocultar Contraseña" else "Mostrar Contraseña"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            if (passError != null) {
                Text(passError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = confirm,
                onValueChange = onConfirmChange,
                label = {Text("Confirmar Contraseña")},
                singleLine = true,
                isError = confirmError != null,
                visualTransformation = if(showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = {showConfirm = !showConfirm}) {
                        Icon(
                            imageVector = if(showConfirm) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showConfirm) "Ocultar confirmacion" else "Mostrar confirmacion"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            AnimatedVisibility(visible = confirmError != null) {
                if (confirmError != null) Text(confirmError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onSubmit,
                enabled = canSubmit && !isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                if(isSubmitting){
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp, modifier = Modifier.size(19.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Creando cuenta...")
                } else {
                    Text("Registrar")
                }
            }

            AnimatedVisibility(visible = errorMsg != null) {
                if (errorMsg != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            OutlinedButton(onClick = onGoLogin, modifier = Modifier.fillMaxWidth()) {
                Text("Ir a Login")
            }
        }
    }
}