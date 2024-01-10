package com.example.yourweather

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yourweather.database.WeatherDao
import com.example.yourweather.screens.MainScreen
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.location.FusedLocationProviderClient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val API_KEY = "7ea8a53b59324322a25220845240701"

class MainActivity : ComponentActivity() {
    private lateinit var weatherDao: WeatherDao

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            val systemUiController: SystemUiController = rememberSystemUiController()

            LaunchedEffect(true) {
                // Скрыть statusBar
                systemUiController.isStatusBarVisible = false // Status bar

                systemUiController.setStatusBarColor(Color.Black)

            }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .systemBarsPadding()
                ) {
                    item{
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth().padding(10.dp)
                        ) {
                            IconButton(onClick = { /*TODO*/ },) {
                                Icon(painter = painterResource(id = R.drawable.baseline_density_small_24,)
                                    , contentDescription = "",tint = Color.White, modifier = Modifier.padding(start = 10.dp).size(50.dp))
                            }
                            IconButton(onClick = { /*TODO*/ },) {
                                Icon(painter = painterResource(id = R.drawable.baseline_share_location_24,)
                                    , contentDescription = "",tint = Color.White,modifier = Modifier.padding(end = 10.dp).size(50.dp))
                            }
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Text(text = "Погода", style = TextStyle(fontSize = 50.sp, color = Color.White,
                            ),
                                modifier = Modifier.padding(70.dp))
                        }
                        MainScreen()
                    }
                }
        }
    }


    override fun onStart() {
        super.onStart()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }
}




private fun convertToData(time:Long):String{
    val sdf = SimpleDateFormat("EEE, d MMMM HH:mm", Locale("ru"))
    return sdf.format(Date(time))
}

//
//
//private fun getResult(city: String, state: MutableState<String>, context: Context) {
//    Log.d("MyLog", "wqweeqwe")
//    val url = "https://api.weatherapi.com/v1/current.json" +
//            "?key=$API_KEY&" +
//            "q=$city" +
//            "&aqi=no"
//
//    val queue = Volley.newRequestQueue(context)
//    val stringRequest = StringRequest(
//        0,
//        url,
//        { response ->
////            state.value = response
//            Log.d("MyLog", response)
//
//
//        },
//        { error ->
//            Log.d("MyLog", error.toString())
//
//        }
//
//    )
//    queue.add(stringRequest)
//
//}












