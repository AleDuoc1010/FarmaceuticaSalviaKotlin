package com.example.farmaceuticasalvia.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.farmaceuticasalvia.ui.viewmodel.ProductViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun CartModal(
    productViewModel: ProductViewModel
){
    val selectedProduct by productViewModel.selectedProduct.collectAsState()
    val modalUiState by productViewModel.modalUiState.collectAsState()
    val quantityError by productViewModel.quantityError.collectAsState()

    if(selectedProduct != null){
        AlertDialog(
            onDismissRequest = {productViewModel.onModalDismiss()},

            title = {
                Text("Añadir ${selectedProduct!!.name}")
            },

            text = {
                Column {
                    OutlinedTextField(
                        value = modalUiState.quantity,
                        onValueChange = {productViewModel.onQuantityChanged(it)},
                        label = {Text("Cantidad")},
                        isError = quantityError != null,
                        supportingText = {
                            if (quantityError != null) Text(quantityError!!)
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {productViewModel.onConfirmCart()}
                ) {
                    Text("Confirmar")
                }
            },

            dismissButton = {
                Button(
                    onClick = {productViewModel.onModalDismiss()}
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}