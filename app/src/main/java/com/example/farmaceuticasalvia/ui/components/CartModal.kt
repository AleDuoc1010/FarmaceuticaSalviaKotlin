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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.farmaceuticasalvia.ui.viewmodel.ProductViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun createTempImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = File(context.cacheDir, "images").apply {
        if (!exists()) mkdirs()
    }
    return File(storageDir, "IMG_${timeStamp}.jpg")
}

private fun getImageUriForFile(context: Context, file: File): Uri {
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context, authority, file)
}

@Composable
fun CartModal(
    productViewModel: ProductViewModel
){
    val selectedProduct by productViewModel.selectedProduct.collectAsState()
    val modalUiState by productViewModel.modalUiState.collectAsState()
    val quantityError by productViewModel.quantityError.collectAsState()
    val recipeError by productViewModel.recipeError.collectAsState()

    val context = LocalContext.current
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            pendingCaptureUri?.let {
                productViewModel.onPhotoTaken(it.toString())
                Toast.makeText(context, "Receta capturada", Toast.LENGTH_SHORT).show()
            }
        } else {
            pendingCaptureUri = null
            Toast.makeText(context, "No se tomó ninguna foto", Toast.LENGTH_SHORT).show()
        }
    }

    if(selectedProduct != null){
        AlertDialog(
            onDismissRequest = {productViewModel.onModalDismiss()},

            title = {
                Text("Añadir ${selectedProduct!!.nombre}")
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
                        ),
                        enabled = !modalUiState.isLoading
                    )
                    Spacer(Modifier.height(8.dp))

                    if (selectedProduct!!.pideReceta) {
                        CameraSectionForCart(
                            photoUriString = modalUiState.photoUriString,
                            error = recipeError,
                            enabled = !modalUiState.isLoading,
                            onTakePhoto = {
                                val file = createTempImageFile(context)
                                val uri = getImageUriForFile(context, file)
                                pendingCaptureUri = uri
                                takePictureLauncher.launch(uri)
                            },
                            onDeletePhoto = {
                                productViewModel.onDeletePhoto()
                            }
                        )
                    }
                    if (modalUiState.errorMessage != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = modalUiState.errorMessage!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {productViewModel.onConfirmCart()},
                    enabled = !modalUiState.isLoading
                ) {
                    if (modalUiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Añadir")
                    }
                }
            },

            dismissButton = {
                Button(
                    onClick = {productViewModel.onModalDismiss()},
                    enabled = !modalUiState.isLoading
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun CameraSectionForCart(
    photoUriString: String?,
    error: String?,
    enabled: Boolean,
    onTakePhoto: () -> Unit,
    onDeletePhoto: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Receta Médica Requerida",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))

        if (!photoUriString.isNullOrEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(Uri.parse(photoUriString)).crossfade(true)
                    .build(),
                contentDescription = "Foto de la receta",
                modifier = Modifier.fillMaxWidth().height(150.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(8.dp))
        }

        if (error != null) {
            Text(error, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(4.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onTakePhoto,
                enabled = enabled
            ) {
                Text(if (photoUriString == null) "Tomar foto" else "Tomar denuevo")
            }
            if (!photoUriString.isNullOrEmpty()) {
                OutlinedButton(
                    onClick = onDeletePhoto,
                    enabled = enabled
                ) {
                    Text("Borrar")
                }
            }
        }
    }
}