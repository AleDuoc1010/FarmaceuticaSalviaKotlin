package com.example.farmaceuticasalvia.ui.screen

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.farmaceuticasalvia.data.local.storage.IpStorage
import com.example.farmaceuticasalvia.ui.theme.Beige

@Composable
fun LoginScreenVm(
    vm: AuthViewModel,
    onLoginOkNavigateHome : () -> Unit,
    onGoRegister : () -> Unit,
){
    val state by vm.login.collectAsStateWithLifecycle()

    LaunchedEffect(state.success) {
        if (state.success) {
            onLoginOkNavigateHome()
            vm.clearLoginResult()
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
fun LoginScreen(
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

    var showIpDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val ipStorage = remember { IpStorage(context) }
    var currentIp by remember { mutableStateOf(ipStorage.getBaseIp()) }

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

        Box(modifier = Modifier.fillMaxSize().padding(top = 24.dp, end = 8.dp), contentAlignment = Alignment.TopEnd) {
            IconButton(onClick = { showIpDialog = true }) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Configurar IP",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Column(

            modifier = Modifier.fillMaxWidth()
                .animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Inicio de Sesión",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(12.dp))

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
                modifier = Modifier.fillMaxWidth().testTag("emailInput")
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
            AnimatedVisibility(
                visible = passError != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                if (passError != null) {
                    Text(
                        passError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
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

                AnimatedVisibility(visible = errorMsg != null) {
                    if (errorMsg != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = errorMsg,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                OutlinedButton(onClick = onGoRegister, modifier = Modifier.fillMaxWidth()) {
                    Text("Crear Cuenta")
                }
            }
            if (showIpDialog) {
                AlertDialog(
                    onDismissRequest = { showIpDialog = false },
                    title = { Text("Configurar IP del Servidor") },
                    text = {
                        Column {
                            Text("Ingresa la IP de tu computador (ej: 192.168.1.5)")
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = currentIp,
                                onValueChange = { currentIp = it },
                                label = { Text("Dirección IP") },
                                singleLine = true
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("Nota: Debes reiniciar la app para aplicar cambios.", style = MaterialTheme.typography.labelSmall)
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            ipStorage.saveBaseIp(currentIp)
                            showIpDialog = false
                            Toast.makeText(context, "IP Guardada. Reinicia la App.", Toast.LENGTH_LONG).show()
                        }) {
                            Text("Guardar")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showIpDialog = false }) { Text("Cancelar") }
                    }
                )
            }
        }
    }