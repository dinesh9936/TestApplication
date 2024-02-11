package com.rama.apps.testapplication.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rama.apps.testapplication.R
import com.rama.apps.testapplication.data.model.Video
import com.rama.apps.testapplication.utils.AppData

@Composable
fun HomeScreenItem(
    item: Video,
    onItemClick: () -> Unit

){
    Card(
        modifier = Modifier
            .height(300.dp)
            .fillMaxWidth()
            .padding(10.dp),
        shape = RoundedCornerShape(1),
        elevation = CardDefaults.elevatedCardElevation(8.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp),
            verticalArrangement = Arrangement.Center
        ){
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.thumb)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.placeholder),
                contentDescription = stringResource(R.string.thumnailDescription),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(200.dp)
                    .clip(RectangleShape)
                    .clickable {
                        AppData.VIDEO_URL = item.sources[0].replace("http://", "https://")
                        onItemClick()
                    }
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),

            ) {
                Text(
                    text = item.title,
                    fontSize = 18.sp
                    )
                Text(
                    text = item.subtitle,
                    fontSize = 15.sp)
            }
        }


    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenItemPreview(){
//    HomeScreenItem(VideoResponseItem(
//        "",
//        ""
//    ))
}


