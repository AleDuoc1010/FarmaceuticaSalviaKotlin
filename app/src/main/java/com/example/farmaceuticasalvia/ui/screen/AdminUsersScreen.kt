package com.example.farmaceuticasalvia.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.farmaceuticasalvia.data.remote.dto.UsuarioDto
import com.example.farmaceuticasalvia.ui.theme.Beige
import com.example.farmaceuticasalvia.ui.viewmodel.AdminUsersViewModel

@Composable
fun AdminUsersScreen(viewModel: AdminUsersViewModel) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Estado para el diálogo de confirmación
    var userToDelete by remember { mutableStateOf<UsuarioDto?>(null) }

    LaunchedEffect(state.errorMsg) {
        if (state.errorMsg != null) {
            Toast.makeText(context, state.errorMsg, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Beige)
            .padding(16.dp)
    ) {
        Text(
            "Panel de Usuarios",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Gestión de administradores y clientes",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading && state.users.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.users) { user ->
                    UserItemCard(
                        user = user,
                        currentAdminUuid = state.currentAdminUuid,
                        onDeleteClick = { userToDelete = user }
                    )
                }
            }
        }
    }

    // Diálogo de Confirmación
    if (userToDelete != null) {
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = { Text("Eliminar Usuario") },
            text = { Text("¿Estás seguro de que deseas eliminar a ${userToDelete?.nombre}? Esta acción es irreversible.") },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    onClick = {
                        userToDelete?.let { viewModel.deleteUser(it.uuid) }
                        userToDelete = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { userToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun UserItemCard(
    user: UsuarioDto,
    currentAdminUuid: String,
    onDeleteClick: () -> Unit
) {
    val isMe = user.uuid == currentAdminUuid
    val isAdmin = user.rol == "ADMINISTRADOR"

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isAdmin) Icons.Filled.Security else Icons.Filled.Person,
                    contentDescription = null,
                    tint = if (isAdmin) Color.Red else Color.Blue,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isMe) "${user.nombre} (Tú)" else user.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = user.email, style = MaterialTheme.typography.bodySmall)

                    // Badge de Rol
                    Surface(
                        color = if (isAdmin) Color(0xFFFFEBEE) else Color(0xFFE3F2FD),
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = user.rol,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isAdmin) Color.Red else Color.Blue
                        )
                    }
                }
            }

            // Botón Eliminar (Deshabilitado si soy yo)
            IconButton(
                onClick = onDeleteClick,
                enabled = !isMe // <--- PROTECCIÓN: No me puedo borrar a mí mismo
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Borrar",
                    tint = if (isMe) Color.LightGray else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}