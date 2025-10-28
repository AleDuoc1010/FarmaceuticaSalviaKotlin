package com.example.farmaceuticasalvia.ui.components

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.request.ImageRequest


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
fun BuyModal(
    productViewModel: ProductViewModel
){
    val selectedProduct by productViewModel.selectedProduct.collectAsState()
    val modalUiState by productViewModel.modalUiState.collectAsState()
    val quantityError by productViewModel.quantityError.collectAsState()
    val phoneError by productViewModel.phoneError.collectAsState()
    val recipeError by productViewModel.recipeError.collectAsState()

    val context = LocalContext.current
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success){
            pendingCaptureUri?.let {
                // Llama al ViewModel en lugar de a un estado local
                productViewModel.onPhotoTaken(it.toString())
                Toast.makeText(context,"Receta capturada", Toast.LENGTH_SHORT).show()
            }
        }else{
            pendingCaptureUri = null
            Toast.makeText(context,"No se tomó ninguna foto", Toast.LENGTH_SHORT).show()
        }
    }

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
                    Spacer(Modifier.height(8.dp))

                    if(selectedProduct!!.requireRecipe){
                        CameraSection(
                            photoUriString = modalUiState.photoUriString,
                            error = recipeError,
                            onTakePhoto = {
                                val file = createTempImageFile(context)
                                val uri = getImageUriForFile(context,file)
                                pendingCaptureUri = uri
                                takePictureLauncher.launch(uri)
                            },
                            onDeletePhoto = {
                                productViewModel.onDeletePhoto()
                                Toast.makeText(context, "Foto eliminada", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
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

@Composable
private fun CameraSection(
    photoUriString: String?,
    error: String?,
    onTakePhoto: () -> Unit,
    onDeletePhoto: () -> Unit
) {
    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            "Receta Médica Requerida",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp ))

        if(!photoUriString.isNullOrEmpty()){
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(Uri.parse(photoUriString)).crossfade(true)
                    .build(),
                contentDescription = "Foto de la receta",
                modifier = Modifier.fillMaxWidth().height(120.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(8.dp))
        }

        if(error != null){
            Text(error, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(4.dp))
        }

        Row (horizontalArrangement = Arrangement.spacedBy(8.dp)){
            Button(onClick = onTakePhoto) {
                Text(if (photoUriString == null ) "Tomar foto " else "Tomar denuevo")
            }
            if(!photoUriString.isNullOrEmpty()){
                OutlinedButton(onClick = onDeletePhoto) {
                    Text("Borrar")
                }
            }
        }

    }
}
