package com.rama.apps.testapplication.ui.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.rama.apps.testapplication.ui.components.AppProgressbar


/**
 * Created by Dinesh on 12/02/24.
 * dineshkumarcse0060@gmail.com
 */

@Composable
fun SplashScreen(
    navController: NavController = rememberNavController(),
    splashViewModels: SplashViewModel = hiltViewModel<SplashViewModel>(),
){

    val firebaseAuth = FirebaseAuth.getInstance()
    val user = firebaseAuth.currentUser


    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AppProgressbar(showProgress = true)
    }


//    LaunchedEffect(key1  = user){
//        delay(2000)
//        if (user != null){
//            navController.navigate(Screens.Home.route)
//        }else{
//            navController.navigate(Screens.Login.route)
//        }
//    }
}
