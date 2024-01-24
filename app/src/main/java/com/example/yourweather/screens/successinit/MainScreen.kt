package com.example.yourweather.screens.successinit


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yourweather.FinderDescription
import com.example.yourweather.R
import com.example.yourweather.ui.theme.CardBackgroundSecondV
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import com.example.yourweather.models.AppState


@Composable
fun MainScreen(
    data:AppState.SuccessInit,
    updateWeather:()->Unit,
) {
    val style = TextStyle(
        fontSize = 18.sp,
        color = Color.White
    )
    val time = remember {
        mutableStateOf("")
    }
    val dateTime = remember {
        mutableStateOf(System.currentTimeMillis())
    }
    time.value = convertToDataSimple(dateTime.value)

    val icon = remember {
        mutableStateOf(R.drawable.sunny)
    }
    val desc = remember {
        mutableStateOf("Ясно")
    }
    val text = data.weatherScreen.current.condition.text
    LaunchedEffect(key1 = text, block ={
        icon.value = FinderDescription.getFoto(text)
        desc.value = FinderDescription.getDescription(text)
    } )

    val descr = FinderDescription.getDescription(data.weatherScreen.current.condition.text)
    Log.d("MyLog","FinderDescription $descr")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = CardBackgroundSecondV,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween ,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(painter = painterResource(id = R.drawable.ic_location_24), contentDescription = "city_loc" )
                            Text(
                                modifier = Modifier.padding(start = 5.dp),
                                text = data.location,
                                style = TextStyle(
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }

                    IconButton(onClick = {
                        dateTime.value = System.currentTimeMillis()
                        updateWeather()
                    }) {
                        Icon(painter = painterResource(id = R.drawable.baseline_cloud_sync_24), contentDescription = "sync",
                            tint = Color.White)
                    }
                }

                Text(
                    modifier = Modifier.padding(start = 5.dp, top = 5.dp),
                    text = time.value,
                    style = TextStyle(
                        fontSize = 15.sp,
                        color = Color.White
                    )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Image(painter = painterResource(id = icon.value), contentDescription = "img",
                        modifier = Modifier.size(64.dp))

                    Text(text = "${data.weatherScreen.current.temp_c}℃", style = TextStyle(
                        color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 50.sp),
                        modifier = Modifier.padding(start = 10.dp)
                    )
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.SpaceAround

                    ){
                        val today = data.weatherScreen.forecast.forecastday[0]
                        Text(text = desc.value,style = style)
                        Text(text = "${today.day.maxtemp_c.toInt()} / ${today.day.mintemp_c.toInt()}",style = style)
                    }
                }
                LazyRow(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(30.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    data.weatherScreen.forecast.forecastday[0].hour.forEach { hourOfDay ->
                            val timeNow = convertToData(hourOfDay.time_epoch)
                            item {
                                HourWeather(time = timeNow, urlImage = hourOfDay.condition.icon, tempC = hourOfDay.temp_c.toFloat())
                            }
                        }
                    }
                }
            }
        Card(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = CardBackgroundSecondV,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
           Column(modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
           ){
               data.weatherScreen.forecast.forecastday.forEach { forecastDay ->
                   val dayOfWeek = convertToWeekDay(forecastDay.date_epoch)
                   DayWeather(
                       time = dayOfWeek,
                       avghumidity = forecastDay.day.avghumidity,
                       url = forecastDay.day.condition.icon,
                       minTemp = forecastDay.day.mintemp_c,
                       maxTemp =forecastDay.day.maxtemp_c,
                       )
                }
            }
        }
        Card(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = CardBackgroundSecondV,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            data.weatherScreen.current.let { current ->
                val uvString = when(current.uv.toInt()){
                    
                    in 0..2 -> "Низкий"
                    in  3..5 -> "Умеренный"
                    in 6..7 -> "Высокий"
                    in  8..10-> "Очень высокий"
                    else -> "Экстремальный"

                }
                ExtraCard(info = current.gust_kph.toString()+"Км/Ч", descriptor = "Скорость ветра", idFotoInt = R.drawable.veter)
                ExtraCard(info = uvString, descriptor = "УФ-излучение", idFotoInt = R.drawable.uf)
                ExtraCard(info = current.humidity.toString()+"%", descriptor ="Влажность", idFotoInt = R.drawable.water)
            }
        }
    }
}


private fun convertToData(time: Long): String {
    val instant = Instant.ofEpochSecond(time)
    val zoneId = ZoneId.of("Europe/Moscow") // Выберите нужный часовой пояс
    val formatter = DateTimeFormatter.ofPattern("HH:mm").withLocale(Locale("ru"))
    return formatter.format(instant.atZone(zoneId))
}
private fun convertToDataSimple(time:Long):String{
    val sdf = SimpleDateFormat("EEE, d MMMM HH:mm", Locale("ru"))
    return sdf.format(Date(time))
}
private fun convertToWeekDay(time: Long): String {
    val instant = Instant.ofEpochSecond(time)
    val zoneId = ZoneId.of("Europe/Moscow") // Выберите нужный часовой пояс
    val formatter = DateTimeFormatter.ofPattern("EEEE").withLocale(Locale("ru"))
    return formatter.format(instant.atZone(zoneId))
}



