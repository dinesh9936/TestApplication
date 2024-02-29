package com.rama.apps.testapplication.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.rama.apps.testapplication.ui.base.Screens
import com.rama.apps.testapplication.ui.theme.Primary
import com.rama.apps.testapplication.utils.UserPreferences


/**
 * Created by Dinesh on 11/02/24.
 * dkcse0060@gmail.com
 */

@Composable
fun SettingScreen(
    navController: NavController,
    userPreferences: UserPreferences,
    settingViewModel: SettingViewModel = hiltViewModel<SettingViewModel>()
) {
    var checked by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(userPreferences){
        checked = userPreferences.setAutoPlay
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AdView(adUnitId = "ca-app-pub-3940256099942544/6300978111")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Set AutoPlay"
            )
            Spacer(modifier = Modifier.width(20.dp))
            Switch(
                checked = checked,
                onCheckedChange = {
                    checked = it
                    userPreferences.setAutoPlay = checked
                },
                thumbContent = if (checked) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize / 4), // Adjust the size as needed
                        )
                    }
                } else {
                    null
                },
                modifier = Modifier.height(SwitchDefaults.IconSize / 4), // Adjust the height as needed
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Primary,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                )
            )



        }

        Button(
            onClick = {
                settingViewModel.logOut()
                navController.navigate(route = Screens.Login.route)
        },
            shape = RectangleShape
        ) {
            Text(text = "LogOut")
        }

    }

}

@Composable
fun AdView(adUnitId: String){
    AndroidView(
        modifier = Modifier
            .fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdUnitId(adUnitId)
                setAdSize(AdSize.BANNER)
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SettingsPreview(){
    //SettingScreen()
}