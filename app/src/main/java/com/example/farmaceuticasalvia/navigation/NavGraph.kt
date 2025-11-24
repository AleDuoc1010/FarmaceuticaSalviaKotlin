package com.example.farmaceuticasalvia.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.farmaceuticasalvia.ui.components.AppDrawer
import com.example.farmaceuticasalvia.ui.components.AppTopBar
import com.example.farmaceuticasalvia.ui.components.defaultDrawerItems
import com.example.farmaceuticasalvia.ui.screen.CartScreen
import com.example.farmaceuticasalvia.ui.screen.HomeScreen
import com.example.farmaceuticasalvia.ui.screen.LoginScreenVm
import com.example.farmaceuticasalvia.ui.screen.ProductScreen
import com.example.farmaceuticasalvia.ui.screen.RegisterScreenVm
import com.example.farmaceuticasalvia.ui.viewmodel.AuthViewModel
import com.example.farmaceuticasalvia.ui.viewmodel.ProductViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.farmaceuticasalvia.domain.validation.showPurchaseNotification
import com.example.farmaceuticasalvia.ui.screen.AdminUsersScreen
import com.example.farmaceuticasalvia.ui.screen.HistoryScreen
import com.example.farmaceuticasalvia.ui.viewmodel.AdminUsersViewModel
import com.example.farmaceuticasalvia.ui.viewmodel.CartViewModel
import com.example.farmaceuticasalvia.ui.viewmodel.HistoryViewModel
import com.example.farmaceuticasalvia.data.local.storage.UserPreferences
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(navController: NavHostController,
                authViewModel: AuthViewModel,
                productViewModel: ProductViewModel,
                cartViewModel: CartViewModel,
                historyViewModel: HistoryViewModel,
                adminUsersViewModel: AdminUsersViewModel,
                userPreferences: UserPreferences){

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val isLoggedIn by userPreferences.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)
    val userRole by userPreferences.userRole.collectAsStateWithLifecycle(initialValue = "")
    val isAdmin = userRole == "ADMINISTRADOR"

    val goHome: () -> Unit  = {navController.navigate(Route.Home.path)}
    val goLogin: () -> Unit = {navController.navigate(Route.Login.path)}
    val goRegister: () -> Unit = {navController.navigate(Route.Register.path)}
    val goProducts: () -> Unit = {navController.navigate(Route.Products.path)}
    val goCart: () -> Unit = {navController.navigate(Route.Cart.path)}
    val goHistory: () -> Unit = {navController.navigate(Route.History.path)}
    val goUsers: () -> Unit = {navController.navigate(Route.Users.path)}

    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        productViewModel.showPurchaseNotificationEvent.collect { productName ->
            showPurchaseNotification(context, productName)
        }
    }

    val handleLogout = {
        scope.launch {
            userPreferences.clearSession()
            drawerState.close()
            navController.navigate(Route.Login.path) {
                popUpTo(0)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = null,
                items = defaultDrawerItems(
                    onHome = {
                        scope.launch { drawerState.close() }
                        goHome()
                    },
                    onLogin = {
                        scope.launch { drawerState.close() }
                        goLogin()
                    },
                    onRegister = {
                        scope.launch { drawerState.close() }
                        goRegister()
                    },
                    onProducts = {
                        scope.launch { drawerState.close() }
                        goProducts()
                    },
                    onCart = {
                        scope.launch { drawerState.close() }
                        goCart()
                    },
                    onHistory = {
                        scope.launch { drawerState.close() }
                        goHistory()
                    },
                    onUsers = {
                        scope.launch { drawerState.close() }
                        goUsers()
                    },
                    isAdmin = isAdmin,
                    isLoggedIn = isLoggedIn
                ),
                isLoggedIn = isLoggedIn,
                onLogout = { handleLogout() }
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    onOpenDrawer = {scope.launch { drawerState.open() }},
                )
            }
        ){ innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Route.Home.path,
                modifier = Modifier.padding(innerPadding)
            ){
                composable(Route.Home.path){
                    HomeScreen(
                        productViewModel = productViewModel
                    )
                }

                composable(Route.Login.path){
                    LoginScreenVm(
                        vm = authViewModel,
                        onLoginOkNavigateHome = goHome,
                        onGoRegister = goRegister,
                    )
                }

                composable(Route.Register.path){
                    RegisterScreenVm(
                        vm = authViewModel,
                        onRegisterNavigateLogin = goLogin,
                        onGoLogin = goLogin
                    )
                }

                composable (Route.Products.path){

                    val products by productViewModel.products.collectAsState()
                    ProductScreen(product = products, productViewModel = productViewModel)
                }

                composable(Route.Cart.path){
                    CartScreen(
                        cartViewModel = cartViewModel
                    )
                }

                composable (Route.History.path){
                    HistoryScreen(
                        historyViewModel = historyViewModel
                    )
                }

                composable(Route.Users.path) {
                    AdminUsersScreen(
                        viewModel = adminUsersViewModel
                    )
                }
            }
        }
    }
}