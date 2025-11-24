package com.example.farmaceuticasalvia.ui.screen

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.farmaceuticasalvia.R
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.farmaceuticasalvia.data.local.storage.UserPreferences
import com.example.farmaceuticasalvia.data.remote.dto.ProductoResponse
import com.example.farmaceuticasalvia.data.utils.fixImageUrl
import com.example.farmaceuticasalvia.ui.components.BuyModal
import com.example.farmaceuticasalvia.ui.components.CartModal
import com.example.farmaceuticasalvia.ui.theme.Beige
import com.example.farmaceuticasalvia.ui.theme.Blue
import com.example.farmaceuticasalvia.ui.viewmodel.ActiveModal
import com.example.farmaceuticasalvia.ui.viewmodel.HomeViewModel
import com.example.farmaceuticasalvia.ui.viewmodel.ProductViewModel

@Composable
fun HomeScreen(
    productViewModel: ProductViewModel,
    homeViewModel: HomeViewModel
){
    val context = LocalContext.current

    val userPrefs = remember { UserPreferences(context) }
    val isLoggedIn by userPrefs.isLoggedIn.collectAsStateWithLifecycle(false)

    val homeState by homeViewModel.uiState.collectAsState()

    val featuredProducts by productViewModel.featuredProducts.collectAsState()
    val selectedProduct by productViewModel.selectedProduct.collectAsState()
    val activeModal by productViewModel.activeModal.collectAsState()

    val bg = Beige

    LaunchedEffect(Unit) {
        productViewModel.refreshData()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            if (homeState.isLoading) {
                Text("Cargando indicadores...", style = MaterialTheme.typography.bodySmall)
            } else if (homeState.dolarValue != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1)), // Verde claro
                    modifier = Modifier.padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Filled.AttachMoney,
                            contentDescription = null,
                            tint = Color(0xFF00695C)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Dólar: $${homeState.dolarValue} | UF: $${homeState.ufValue}",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFF004D40),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Farmaceutica Salvia",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = if(isLoggedIn) Icons.Filled.Person else Icons.Filled.PersonOff,
                    contentDescription = if(isLoggedIn) "Usuario Conectado" else "Usuario no conectado",
                    tint = if(isLoggedIn) MaterialTheme.colorScheme.primary else
                    MaterialTheme.colorScheme.outline
                )
            }

            Spacer(Modifier.width(20.dp))

            Text("Productos Destacados",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(featuredProducts){producto ->
                    FeaturedProductCard(
                        product = producto,
                        onBuyClick = {
                            productViewModel.onProductSelected(producto, ActiveModal.BUY)
                        },
                        onCartClick = {
                            productViewModel.onProductSelected(producto, ActiveModal.CART)
                        }
                    )
                }
            }
            Spacer(Modifier.height(20.dp))


        }
    }
    if(selectedProduct != null && activeModal == ActiveModal.BUY) {
        BuyModal(productViewModel = productViewModel)
    }
    if(selectedProduct != null && activeModal == ActiveModal.CART){
        CartModal(productViewModel = productViewModel)
    }
}
@Composable
private fun FeaturedProductCard(
    product: ProductoResponse,
    onBuyClick: () -> Unit,
    onCartClick: () -> Unit
){
    ElevatedCard(
        modifier = Modifier
            .width(180.dp)
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(fixImageUrl(LocalContext.current,product.imagenUrl))
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                error = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = product.nombre,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                product.nombre,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            Text(
                "$${product.precio.toInt()}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))

            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Button(
                    onClick = onBuyClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Blue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Comprar")
                }
                Spacer(Modifier.height(6.dp))

                OutlinedButton(
                    onClick = onCartClick,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Blue
                    ),
                    border = BorderStroke(1.dp, Blue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Añadir")
                }
            }
        }
    }
}
