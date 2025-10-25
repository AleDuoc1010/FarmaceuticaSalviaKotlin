package com.example.farmaceuticasalvia.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextOverflow
import com.example.farmaceuticasalvia.ui.theme.Blue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onOpenDrawer: () -> Unit,
){

    var showMenu by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Blue
        ),
        title = {
            Text(
                text = "Farmaceutica Salvia",
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.surface
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onOpenDrawer) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menú", tint = MaterialTheme.colorScheme.surface)
            }
        }
    )
}