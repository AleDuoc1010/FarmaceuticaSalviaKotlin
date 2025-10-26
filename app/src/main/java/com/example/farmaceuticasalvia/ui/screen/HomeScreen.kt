package com.example.farmaceuticasalvia.ui.screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.stylusHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import java.util.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.farmaceuticasalvia.data.local.products.ProductEntity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import androidx.compose.foundation.lazy.items
import com.example.farmaceuticasalvia.data.local.storage.UserPreferences
import com.example.farmaceuticasalvia.ui.components.BuyModal
import com.example.farmaceuticasalvia.ui.components.CartModal
import com.example.farmaceuticasalvia.ui.theme.Beige
import com.example.farmaceuticasalvia.ui.theme.Blue
import com.example.farmaceuticasalvia.ui.viewmodel.ActiveModal
import com.example.farmaceuticasalvia.ui.viewmodel.ProductViewModel

private fun createTempImageFile(context: Context): File{
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = File(context.cacheDir, "images").apply {
        if(!exists()) mkdirs()
    }
    return File(storageDir,"IMG_${timeStamp}.jpg")
}

private fun getImageUriForFile(context: Context, file: File): Uri{
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context,authority,file)
}
@Composable
fun HomeScreen(
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit,
    productViewModel: ProductViewModel
){
    val context = LocalContext.current

    val userPrefs = remember { UserPreferences(context) }
    val isLoggedIn by userPrefs.isLoggedIn.collectAsStateWithLifecycle(false)

    val featuredProducts by productViewModel.featuredProducts.collectAsState()
    val selectedProduct by productViewModel.selectedProduct.collectAsState()
    val activeModal by productViewModel.activeModal.collectAsState()

    var photoUriString by rememberSaveable { mutableStateOf<String?>(null) }

    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success){
            photoUriString = pendingCaptureUri.toString()
            Toast.makeText(context,"Foto capturada correctamente", Toast.LENGTH_SHORT).show()
        }else{
            pendingCaptureUri = null
            Toast.makeText(context,"No se tomó ninguna foto", Toast.LENGTH_SHORT).show()
        }
    }

    val bg = Beige

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
                AssistChip(
                    onClick = onGoLogin,
                    label = {Text("Navega")}
                )
            }

            Spacer(Modifier.width(20.dp))

            Text("Productos Destacados",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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

            ElevatedCard (
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp)
            ){
                Column (
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = "Captura de foto con cámara",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))

                    if(photoUriString.isNullOrEmpty()){
                        Text(
                            text = "No ha tomado fotos",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(12.dp))
                    } else{
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(Uri.parse(photoUriString)).crossfade(true)
                                .build(),
                            contentDescription = "Foto Tomada",
                            modifier = Modifier.fillMaxWidth().height(150.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    var showDialog by remember { mutableStateOf(false) }

                    Button(onClick = {
                        val file = createTempImageFile(context)
                        val uri = getImageUriForFile(context,file)
                        pendingCaptureUri = uri
                        takePictureLauncher.launch(uri)
                    }) {
                        if(photoUriString.isNullOrEmpty()) "Abrir Cámara"
                        else "Volver a tomar foto"
                    }
                    if(!photoUriString.isNullOrEmpty()){
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(onClick = {showDialog = true}) {
                            Text("Eliminar foto")
                        }
                    }

                    if(showDialog){
                        AlertDialog(
                            onDismissRequest = {showDialog = false},
                            title = {Text("Confirmación")},
                            text = {Text("¿Desea eliminar la foto?")},
                            confirmButton = {
                                TextButton( onClick = {
                                    photoUriString = null
                                    showDialog = false
                                    Toast.makeText(context, "Foto eliminada correctamente", Toast.LENGTH_SHORT).show()
                            }   ){
                                    Text("Aceptar")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {showDialog = false}) {
                                    Text("Cancelar")
                                }
                            }
                        )
                    }
                }
            }

            Row (
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ){
                Button(onClick = onGoLogin) { Text("ir a login")}
                OutlinedButton(onClick = onGoRegister) { Text("ir a register") }
            }
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
    product: ProductEntity,
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
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.name,
                modifier = Modifier.size(80.dp),
                alignment = Alignment.Center
            )
            Text(
                product.name,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                "$${product.price}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(12.dp))

            Row (horizontalArrangement = Arrangement.spacedBy(4.dp)){
                Button(
                    onClick = onBuyClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Blue),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Comprar", style = MaterialTheme.typography.labelSmall)
                }
                OutlinedButton(
                    onClick = onCartClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Blue),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Añadir", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
