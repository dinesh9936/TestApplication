package com.rama.apps.testapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.*
import androidx.navigation.findNavController
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.initialize
import com.rama.apps.testapplication.ui.base.Screens
import com.rama.apps.testapplication.ui.screens.home.HomeScreen
import com.rama.apps.testapplication.ui.screens.login.LoginScreen
import com.rama.apps.testapplication.ui.screens.settings.SettingScreen
import com.rama.apps.testapplication.ui.theme.Primary
import com.rama.apps.testapplication.ui.theme.TestApplicationTheme
import com.rama.apps.testapplication.utils.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance(),
        )
        setContent {
            App(userPreferences, this)
        }


    }
}

@Composable
fun App(
    userPreferences: UserPreferences,
    activity: MainActivity,
    mainActivityViewModel: MainActivityViewModel = hiltViewModel<MainActivityViewModel>(),
) {
    val navController = rememberNavController()
    val screens = listOf(Screens.Home, Screens.Settings)

    var isLoggedIn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoggedIn = userPreferences.setLoggedIn
    }

    // Use LaunchedEffect to navigate when isLoggedIn changes
    LaunchedEffect(isLoggedIn) {
        navController.navigate(if (isLoggedIn) Screens.Home.route else Screens.Login.route) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
        }
    }

    TestApplicationTheme {
        NavHost(navController, startDestination = Screens.Login.route) {
            composable(Screens.Home.route) {
                HomeContent(navController, userPreferences)
            }
            composable(Screens.Settings.route) {
                SettingContent(navController, userPreferences)
            }
            composable(Screens.Login.route) {
                LoginContent(navController, activity)
            }
        }
    }
}

// The rest of your composable functions remain unchanged



@Composable
fun HomeContent(
    navController: NavController,
    userPreferences: UserPreferences,
) {
    Scaffold(
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .height(56.dp),
                contentColor = Primary,
                containerColor = Primary,
                contentPadding = PaddingValues(8.dp)
            ) {
                val screens = listOf(Screens.Home, Screens.Settings)
                val icons = listOf(Icons.Outlined.Home, Icons.Outlined.Settings)

                screens.forEachIndexed { index, screen ->
                    IconButton(
                        onClick = {
                            navController.navigate(screen.route)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Column {
                            Icon(
                                imageVector = icons[index],
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HomeScreen(navController)
        }
    }
}

@Composable
fun SettingContent(
    navController: NavController,
    userPreferences: UserPreferences
) {
    Scaffold(
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .height(56.dp),
                contentColor = Primary,
                containerColor = Primary,
                contentPadding = PaddingValues(8.dp)
            ) {
                val screens = listOf(Screens.Home, Screens.Settings)
                val icons = listOf(Icons.Outlined.Home, Icons.Outlined.Settings)

                screens.forEachIndexed { index, screen ->
                    IconButton(
                        onClick = {
                            navController.navigate(screen.route)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Column {
                            Icon(
                                imageVector = icons[index],
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SettingScreen(navController, userPreferences)
        }
    }
}

@Composable
fun LoginContent(
    navController: NavController,
    activity: MainActivity
) {
    LoginScreen(navController = navController, activity = activity)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestApplicationTheme {
        //App()
    }
}
