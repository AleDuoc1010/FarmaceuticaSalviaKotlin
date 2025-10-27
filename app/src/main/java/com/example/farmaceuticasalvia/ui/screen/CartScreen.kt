package com.example.farmaceuticasalvia.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.farmaceuticasalvia.ui.viewmodel.CartViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.Alignment
import com.example.farmaceuticasalvia.data.local.cart.CartItem
import com.example.farmaceuticasalvia.ui.theme.Beige

@Composable
fun CartScreen(cartViewModel: CartViewModel){

    val cartItems by cartViewModel.cartItems.collectAsState()

    Column (
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
                "Carrito",
                style = MaterialTheme.typography.headlineMedium
            )

            if (cartItems.isNotEmpty()) {
                OutlinedButton(onClick = {cartViewModel.clearCart()}) {
                    Text("Vaciar Carrito")
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        if (cartItems.isEmpty()){
            Text("Tu carrito está vacío.")
        } else{
            LazyColumn (modifier = Modifier.fillMaxWidth()){
                items(cartItems){item ->
                    CartItemRow(
                        item = item,
                        onDelete = {cartViewModel.removeFromCart(item.product.id)})
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}
@Composable
fun CartItemRow(item: CartItem, onDelete: () -> Unit){
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ){
            Image(
                painter = painterResource(id = item.product.imageRes),
                contentDescription = item.product.name,
                modifier = Modifier.size(60.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column (modifier = Modifier.weight(1f)){
                Text(item.product.name, style = MaterialTheme.typography.titleMedium)
                Text("Precio $${item.product.price}")
            }
            Text("x ${item.quantity}", style = MaterialTheme.typography.titleLarge)

            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar producto")
            }
        }
    }

}