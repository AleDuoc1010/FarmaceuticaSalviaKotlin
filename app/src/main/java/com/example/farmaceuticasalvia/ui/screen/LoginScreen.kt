package com.example.farmaceuticasalvia.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.farmaceuticasalvia.data.local.storage.UserPreferences
import com.example.farmaceuticasalvia.ui.theme.Beige

@Composable
fun LoginScreenVm(
    vm: AuthViewModel,
    onLoginOkNavigateHome : () -> Unit,
    onGoRegister : () -> Unit,
){
    val state by vm.login.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }

    LaunchedEffect(state.success) {
        if (state.success) {
            userPrefs.setLoggedIn(true)
            vm.clearLoginResult()
            onLoginOkNavigateHome()
        }
    }

    LoginScreen(
        email = state.email,
        pass = state.pass,

        onSubmit = vm::submitLogin,
        onGoRegister = onGoRegister,

        canSubmit = state.canSubmit,
        isSubmitting = state.isSubmitting,

        emailError = state.emailError,
        passError = state.passError,
        errorMsg = state.errorMsg,

        onEmailChange = vm::onLoginEmailChange,
        onPassChange = vm::onLoginPassChange
    )

}
@Composable
private fun LoginScreen(
    email: String,                                           // Campo email
    pass: String,                                            // Campo contraseña
    emailError: String?,                                     // Error de email
    passError: String?,                                      // Error de password (opcional)
    canSubmit: Boolean,                                      // Habilitar botón
    isSubmitting: Boolean,                                   // Flag loading
    errorMsg: String?,                                       // Error global (credenciales)
    onEmailChange: (String) -> Unit,                         // Handler cambio email
    onPassChange: (String) -> Unit,                          // Handler cambio password
    onSubmit: () -> Unit,                                    // Acción enviar
    onGoRegister: () -> Unit,
) {
    val bg = Beige

    var showPass by remember { mutableStateOf(false) }

    val buttonAlpha by animateFloatAsState(
        targetValue = if (isSubmitting) 0.6f else 1f,
        label = "alphaLoginButton"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(

            modifier = Modifier.fillMaxWidth()
                .animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(12.dp))

            Text(
                text = "Pantalla de login",
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                singleLine = true,
                isError = emailError != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(
                visible = emailError != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                if (emailError != null) {
                    Text(
                        emailError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = pass,
                onValueChange = onPassChange,
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPass) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                isError = passError != null,
                modifier = Modifier.fillMaxWidth()
            )
            if (passError != null) {
                Text(
                    passError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall)
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onSubmit,
                    enabled = canSubmit && !isSubmitting,
                    modifier = Modifier.fillMaxWidth()
                        .alpha(buttonAlpha)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Validando...")
                    } else {
                            Text("Entrar")
                    }
                }

                if (errorMsg != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(errorMsg, color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.height(12.dp))

                OutlinedButton(onClick = onGoRegister, modifier = Modifier.fillMaxWidth()) {
                    Text("Crear Cuenta")
                }
            }
        }
    }