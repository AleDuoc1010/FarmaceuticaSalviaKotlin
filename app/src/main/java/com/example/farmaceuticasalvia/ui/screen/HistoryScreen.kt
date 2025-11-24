package com.example.farmaceuticasalvia.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.farmaceuticasalvia.ui.theme.Beige
import com.example.farmaceuticasalvia.ui.viewmodel.HistoryViewModel
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.farmaceuticasalvia.ui.viewmodel.HistoryItemUi
import com.example.farmaceuticasalvia.ui.viewmodel.HistoryOrderUi
import com.example.farmaceuticasalvia.R
import com.example.farmaceuticasalvia.data.utils.fixImageUrl

@Composable
fun HistoryScreen(historyViewModel: HistoryViewModel) {

    val state by historyViewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        historyViewModel.loadHistory()
    }

    LaunchedEffect(state.errorMsg) {
        if (state.errorMsg != null) {
            Toast.makeText(context, state.errorMsg, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Beige)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Historial de Compras",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            if (state.orders.isNotEmpty()) {
                OutlinedButton(
                    onClick = { historyViewModel.clearHistory() },
                    enabled = !state.isLoading
                ) {
                    Text("Vaciar")
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        // --- CONTENIDO ---
        if (state.orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (state.isLoading) {
                    Text("Cargando historial...")
                } else {
                    Text("Aún no has realizado compras.")
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.orders) { order ->
                    HistoryOrderCard(
                        order = order,
                        onDeleteOrder = { historyViewModel.removeFromHistory(order.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryOrderCard(
    order: HistoryOrderUi,
    onDeleteOrder: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // --- CABECERA DEL PEDIDO ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Pedido #${order.id}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Total: $${order.total.toInt()}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Botón Eliminar Pedido Específico
                IconButton(onClick = onDeleteOrder) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Eliminar Pedido",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            order.items.forEach { item ->
                HistoryItemRow(item = item)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "ID Seguimiento: ${order.uuid}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun HistoryItemRow(item: HistoryItemUi) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Imagen del Producto (Desde URL)
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(fixImageUrl(LocalContext.current,item.imageUrl))
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_launcher_foreground),
            error = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = item.name,
            modifier = Modifier.size(50.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${item.quantity} x $${item.price.toInt()}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}