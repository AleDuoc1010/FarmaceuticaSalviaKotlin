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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.farmaceuticasalvia.data.local.products.ProductEntity
import com.example.farmaceuticasalvia.ui.components.BuyModal
import com.example.farmaceuticasalvia.ui.components.CartModal
import com.example.farmaceuticasalvia.ui.theme.Beige
import com.example.farmaceuticasalvia.ui.theme.Blue
import com.example.farmaceuticasalvia.ui.viewmodel.ActiveModal
import com.example.farmaceuticasalvia.ui.viewmodel.ProductViewModel
import androidx.compose.runtime.getValue

@Composable
fun ProductScreen(
    product: List<ProductEntity>,
    productViewModel: ProductViewModel
){
    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
            .background(Beige)) {

        item {
            Text("Productos", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(50.dp))
        }

        items(product){ producto ->
            ProductItem(product = producto,
                onBuyClick = {
                    productViewModel.onProductSelected(producto, ActiveModal.BUY)
                },
                onCartClick = {
                    productViewModel.onProductSelected(producto, ActiveModal.CART)
                }
            )
        }

        item {
            Spacer(Modifier.height(16.dp))
        }
    }
    val selectedProduct by productViewModel.selectedProduct.collectAsState()
    val activeModal by productViewModel.activeModal.collectAsState()

    if(selectedProduct != null && activeModal == ActiveModal.BUY) {
        BuyModal(productViewModel = productViewModel)
    }

    if(selectedProduct != null && activeModal == ActiveModal.CART){
        CartModal(productViewModel = productViewModel)
    }
}

@Composable
fun ProductItem(
    product: ProductEntity,
    onBuyClick: () -> Unit,
    onCartClick: () -> Unit){

    ElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ){
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.name,
                modifier = Modifier.size(100.dp),
                alignment = Alignment.Center
            )
            Spacer(Modifier.height(16.dp))

            Text(product.name, style = MaterialTheme.typography.titleMedium)
            Text(product.descr)
            Text("Precio: $${product.price}")

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Button(
                    onClick = onBuyClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blue
                    )
                ) {
                    Text("Comprar")
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = onCartClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blue
                    )
                ) {
                    Text("Añadir")
                }
            }
        }
    }
}