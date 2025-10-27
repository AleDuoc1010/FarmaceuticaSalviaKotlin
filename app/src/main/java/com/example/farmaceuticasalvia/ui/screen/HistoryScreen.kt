package com.example.farmaceuticasalvia.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.farmaceuticasalvia.data.local.history.PurchaseRecord
import com.example.farmaceuticasalvia.ui.theme.Beige
import com.example.farmaceuticasalvia.ui.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.stylusHoverIcon
import androidx.lifecycle.viewmodel.compose.viewModel
import java.nio.file.WatchEvent

private fun formatDate(timeStamp: Long): String{
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timeStamp))
}

@Composable
fun HistoryScreen(historyViewModel: HistoryViewModel){

    val history by historyViewModel.history.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Beige)
            .padding(16.dp)
    ){
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                "Historial de compras",
                style = MaterialTheme.typography.headlineMedium
            )

            if (history.isNotEmpty()){
                OutlinedButton(onClick = {historyViewModel.clearHistory()}) {
                    Text("Borrar")
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        if(history.isEmpty()) {
            Text("Aun no has realizado compras.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(history) {record ->
                    HistoryItemCard(
                        record = record,
                        onDelete = {historyViewModel.removeFromHistory(record.id)}
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryItemCard(
    record: PurchaseRecord,
    onDelete: () -> Unit
){
    Card (
        modifier = Modifier.fillMaxWidth()
    ){
        Column( modifier = Modifier.padding(12.dp)) {
            Text(
                record.product.name,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Fecha: ${formatDate(record.timeStamp)}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(8.dp))

            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    "Teléfono: ${record.phone}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "x ${record.quantity}",
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar Registro")
                }
            }
        }
    }
}