package com.example.farmaceuticasalvia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.farmaceuticasalvia.data.local.database.AppDataBase
import com.example.farmaceuticasalvia.data.repository.ProductRepository
import com.example.farmaceuticasalvia.data.repository.UserRepository
import com.example.farmaceuticasalvia.navigation.AppNavGraph
import com.example.farmaceuticasalvia.ui.viewmodel.AuthViewModel
import com.example.farmaceuticasalvia.ui.viewmodel.AuthViewModelFactory
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

    val db = AppDataBase.getInstance(context)

    val userDao = db.userDao()
    val productDao = db.productDao()

    val userRepository = UserRepository(userDao)
    val productRepository = ProductRepository(productDao)

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(userRepository)
    )

    val productViewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(productRepository)
    )

    val navController = rememberNavController()
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background){

            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel,
                productViewModel = productViewModel
            )
        }
    }
}