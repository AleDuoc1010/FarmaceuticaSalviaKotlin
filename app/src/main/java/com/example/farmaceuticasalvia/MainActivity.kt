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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.farmaceuticasalvia.data.local.storage.UserPreferences
import com.example.farmaceuticasalvia.data.remote.api.RetrofitClient
import com.example.farmaceuticasalvia.data.repository.HistoryRepository
import com.example.farmaceuticasalvia.data.repository.ProductRepository
import com.example.farmaceuticasalvia.data.repository.UserRepository
import com.example.farmaceuticasalvia.navigation.AppNavGraph
import com.example.farmaceuticasalvia.ui.viewmodel.AuthViewModel
import com.example.farmaceuticasalvia.ui.viewmodel.AuthViewModelFactory
import com.example.farmaceuticasalvia.data.repository.CartRepository
import com.example.farmaceuticasalvia.data.repository.ExternalRepository
import com.example.farmaceuticasalvia.ui.viewmodel.AdminUsersViewModel
import com.example.farmaceuticasalvia.ui.viewmodel.AdminUsersViewModelFactory
import com.example.farmaceuticasalvia.ui.viewmodel.CartViewModel
import com.example.farmaceuticasalvia.ui.viewmodel.CartViewModelFactory
import com.example.farmaceuticasalvia.ui.viewmodel.HistoryViewModel
import com.example.farmaceuticasalvia.ui.viewmodel.HistoryViewModelFactory
import com.example.farmaceuticasalvia.ui.viewmodel.HomeViewModel
import com.example.farmaceuticasalvia.ui.viewmodel.HomeViewModelFactory
import com.example.farmaceuticasalvia.ui.viewmodel.ProductViewModel
import com.example.farmaceuticasalvia.ui.viewmodel.ProductViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
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
                Log.d("PermissionRequest", "Solicitud permiso de notificación...")
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val userPreferences = UserPreferences(context)

    val usuarioApi = RetrofitClient.getUsuariosApi(context)
    val catalogoApi = RetrofitClient.getCatalogoApi(context)
    val pedidosApi = RetrofitClient.getPedidosApi(context)
    val externalApi = RetrofitClient.getExternalApi(context)

    val userRepository = UserRepository(usuarioApi, userPreferences)
    val productRepository = ProductRepository(catalogoApi)
    val cartRepository = CartRepository(pedidosApi)
    val historyRepository = HistoryRepository(pedidosApi)
    val externalRepository = ExternalRepository(externalApi)


    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(userRepository)
    )

    val productViewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(productRepository, cartRepository)
    )

    val cartViewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(cartRepository, productRepository)
    )

    val historyViewModel: HistoryViewModel = viewModel(
        factory = HistoryViewModelFactory(historyRepository, productRepository)
    )

    val adminUsersViewModel: AdminUsersViewModel = viewModel(
        factory = AdminUsersViewModelFactory(userRepository, userPreferences)
    )

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(externalRepository)
    )

    val navController = rememberNavController()
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background){

            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel,
                productViewModel = productViewModel,
                cartViewModel = cartViewModel,
                historyViewModel = historyViewModel,
                adminUsersViewModel = adminUsersViewModel,
                userPreferences = userPreferences,
                homeViewModel = homeViewModel
            )
        }
    }
}