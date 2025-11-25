package com.example.farmaceuticasalvia.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class DrawerItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun AppDrawer(
    currentRoute: String?,
    items: List<DrawerItem>,
    modifier: Modifier = Modifier,
    isLoggedIn: Boolean,
    onLogout: () -> Unit
){
    ModalDrawerSheet(modifier = modifier){

        Column(modifier = Modifier.fillMaxHeight()) {

            Text(
                "Farmaceutica Salvia",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(24.dp)
            )
            HorizontalDivider()

            items.forEach { item ->
                NavigationDrawerItem(
                    label = { Text(item.label) },
                    selected = false,
                    onClick = item.onClick,
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors()
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (isLoggedIn) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    label = { Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error) },
                    selected = false,
                    onClick = onLogout,
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun defaultDrawerItems(
    onHome: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onProducts: () -> Unit,
    onCart: () -> Unit,
    onHistory: () -> Unit,
    onUsers: () -> Unit,
    isAdmin: Boolean,
    isLoggedIn: Boolean
): List<DrawerItem> {

    val items = mutableListOf(
        DrawerItem("Inicio", Icons.Filled.Home, onHome),
        DrawerItem("Productos", Icons.Filled.Shop, onProducts),
    )

    if (!isLoggedIn) {
        items.add(DrawerItem("Iniciar Sesión", Icons.Filled.AccountCircle, onLogin))
        items.add(DrawerItem("Registrarse", Icons.Filled.Person, onRegister))
    }

    if (isLoggedIn) {
        items.add(DrawerItem("Carrito", Icons.Filled.ShoppingCart, onCart))
        items.add(DrawerItem("Historial", Icons.Filled.History, onHistory))
    }

    if (isAdmin) {
        items.add(DrawerItem("Gestionar Usuarios", Icons.Filled.SupervisedUserCircle, onUsers))
    }

    return items
}