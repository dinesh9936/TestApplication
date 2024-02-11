package com.rama.apps.testapplication.ui.screens.home

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.rama.apps.testapplication.VideoPlayActivity
import com.rama.apps.testapplication.data.model.Video
import com.rama.apps.testapplication.ui.base.Screens
import com.rama.apps.testapplication.ui.components.HomeScreenItem
import com.rama.apps.testapplication.utils.AppData


@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel<HomeViewModel>()
){

    val logTag = "HomeScreenTAG"
    val videos by remember{ mutableStateOf(null) }
    val movies by homeViewModel.videos.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(homeViewModel){

    }


    Column {
        AdView(adUnitId = "ca-app-pub-3940256099942544/6300978111")
        VideoList(items = movies, navController = navController, context)
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

@Composable
fun VideoList(items: List<Video>, navController: NavController, context: Context) {
    for (video in items){
        Log.d("HomeScreenTAG", "Videos: ${video.sources[0]}")
        AppData.videos.add(video.sources[0].replace("http://", "https://"))
    }
    LazyColumn {
        items(items.size) { item ->

            HomeScreenItem(
                item = items[item],
                onItemClick = {
                    AppData.videoIndex = item
                    context.startActivity(Intent(context, VideoPlayActivity::class.java))
                }
            )
        }
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun HomeScreenPreview(){
    //HomeScreen()
}

