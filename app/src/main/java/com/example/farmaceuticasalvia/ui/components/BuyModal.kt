package com.example.farmaceuticasalvia.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.farmaceuticasalvia.ui.viewmodel.ProductViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button

@Composable
fun BuyModal(
    productViewModel: ProductViewModel
){
    val selectedProduct by productViewModel.selectedProduct.collectAsState()
    val modalUiState by productViewModel.modalUiState.collectAsState()
    val quantityError by productViewModel.quantityError.collectAsState()
    val phoneError by productViewModel.phoneError.collectAsState()

    if(selectedProduct != null){
        AlertDialog(
            onDismissRequest = {productViewModel.onModalDismiss()},

            title = {
                Text("Comprar ${selectedProduct!!.name}")
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
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = modalUiState.phone,
                        onValueChange = {productViewModel.onPhoneChanged(it)},
                        label = {Text("Teléfono")},
                        isError = phoneError != null,
                        supportingText = {
                            if (phoneError != null) Text(phoneError!!)
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Phone
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {productViewModel.onConfirmBuy()}
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