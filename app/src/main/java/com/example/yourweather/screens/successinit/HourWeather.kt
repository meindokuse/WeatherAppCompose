package com.example.yourweather.screens.successinit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun HourWeather(
    time:String,
    urlImage:String,
    tempC:Float
){
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = time, style = TextStyle(Color.Gray, fontSize = 16.sp))
        AsyncImage(model = "https:$urlImage", contentDescription = "status_hour" , modifier = Modifier.size(40.dp))
        Text(text = tempC.toString(), style = TextStyle(Color.White, fontSize = 18.sp))
    }
}

@Composable
fun DayWeather(
    time: String,
    avghumidity: Int,
    url: String,
    minTemp: Float,
    maxTemp: Float
) {
    val style = TextStyle(fontSize = 16.sp, color = Color.White)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f) // Занимает всю доступную ширину
        ) {
            Text(text = time, style = style, modifier = Modifier.align(Alignment.CenterStart))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .weight(1f) // Занимает всю доступную ширину
        ) {
            Text(text = "$avghumidity%", style = TextStyle(fontSize = 16.sp, color = Color.Gray))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .weight(1f) // Занимает всю доступную ширину
        ) {
            AsyncImage(model = "https:$url", contentDescription = "img2", modifier = Modifier.size(40.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .weight(1f) // Занимает всю доступную ширину
        ) {
            Text(text = "${minTemp.toInt()}/${maxTemp.toInt()}", style = style, modifier = Modifier.align(Alignment.CenterEnd))
        }
    }
}