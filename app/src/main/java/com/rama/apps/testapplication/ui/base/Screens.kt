package com.rama.apps.testapplication.ui.base

sealed class Screens(val route: String){
    data object Home: Screens(route = "home")
    data object Login: Screens(route = "login")
    data object Settings: Screens(route = "settings")
    data object SplashScreen: Screens(route = "splash")
    data object AppScreen: Screens(route = "app")
}