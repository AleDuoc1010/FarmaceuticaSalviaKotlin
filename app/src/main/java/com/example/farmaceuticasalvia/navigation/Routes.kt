package com.example.farmaceuticasalvia.navigation

sealed class Route(val path: String) {

    data object Home : Route("home")
    data object Login : Route("login")
    data object Register : Route("register")
    data object Products : Route("products")

    data object Cart : Route("Cart")

    data object History : Route("History")

    data object Users : Route("Users")
}