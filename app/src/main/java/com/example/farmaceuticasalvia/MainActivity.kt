package com.example.farmaceuticasalvia

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.farmaceuticasalvia.data.local.database.AppDataBase
import com.example.farmaceuticasalvia.data.repository.ProductRepository
import com.example.farmaceuticasalvia.data.repository.UserRepository
import com.example.farmaceuticasalvia.navigation.AppNavGraph
import com.example.farmaceuticasalvia.ui.viewmodel.AuthViewModel
import com.example.farmaceuticasalvia.ui.viewmodel.AuthViewModelFactory
import com.example.farmaceuticasalvia.ui.viewmodel.CartRepository
import com.example.farmaceuticasalvia.ui.viewmodel.CartViewModel
import com.example.farmaceuticasalvia.ui.viewmodel.CartViewModelFactory
import com.example.farmaceuticasalvia.ui.viewmodel.ProductViewModel
import com.example.farmaceuticasalvia.ui.viewmodel.ProductViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}

@Composable
fun AppRoot() {

    val context = LocalContext.current.applicationContext

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted){
                Log.d("PermissionRequest", "Permiso de notificación concedido")
            } else {
                Log.w("PermissionRequest","Permiso de notificación denegado")
            }
        }
    )

    LaunchedEffect(key1 = Unit) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("PermissionRequest", "Solicitud permisd de notificación...")
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val db = AppDataBase.getInstance(context)

    val userDao = db.userDao()
    val productDao = db.productDao()
    val cartDao = db.CartDao()

    val userRepository = UserRepository(userDao)
    val productRepository = ProductRepository(productDao)
    val cartRepository = CartRepository(cartDao)

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(userRepository)
    )

    val productViewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(productRepository, cartRepository)
    )

    val cartViewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(cartRepository)
    )

    val navController = rememberNavController()
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background){

            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel,
                productViewModel = productViewModel,
                cartViewModel = cartViewModel
            )
        }
    }
}