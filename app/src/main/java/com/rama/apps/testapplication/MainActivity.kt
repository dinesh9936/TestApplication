package com.rama.apps.testapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rama.apps.testapplication.ui.base.Screens
import com.rama.apps.testapplication.ui.screens.home.HomeScreen
import com.rama.apps.testapplication.ui.screens.settings.SettingScreen
import com.rama.apps.testapplication.ui.theme.Purple40
import com.rama.apps.testapplication.ui.theme.TestApplicationTheme
import com.rama.apps.testapplication.utils.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object{
        private const val TAG = "MainActivity"
    }


    @Inject
     lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            App(userPreferences)
        }
    }
}

@Composable
fun App(userPreferences: UserPreferences) {
    val navController = rememberNavController()

    val screens = listOf(Screens.Home, Screens.Settings)
    val icons = listOf(Icons.Outlined.Home, Icons.Outlined.Settings)

    TestApplicationTheme {
        Scaffold(
            bottomBar = {
                BottomAppBar(
                    modifier = Modifier.
                    height(56.dp),
                    contentColor = Color.White,
                    containerColor = Color.White,
                    contentPadding = PaddingValues(8.dp)
                ) {
                    screens.forEachIndexed { index, screen->
                        IconButton(
                            onClick = {
                                navController.navigate(screen.route)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = icons[index],
                                contentDescription = null,
                                modifier  = Modifier.size(30.dp),
                                tint = Purple40
                            )
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

                NavHost(navController, startDestination = Screens.Home.route) {
                    composable(Screens.Home.route) {
                        HomeScreen(navController)
                    }
                    composable(Screens.Settings.route){
                        SettingScreen(userPreferences)
                    }
                }
            }
        }
    }
}






@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestApplicationTheme {
        //App()
    }
}