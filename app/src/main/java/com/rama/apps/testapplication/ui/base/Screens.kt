package com.rama.apps.testapplication.ui.base

sealed class Screens(val route: String){
    data object Home: Screens(route = "home")
    data object Video: Screens(route = "video")
    data object Settings: Screens(route = "settings")
}