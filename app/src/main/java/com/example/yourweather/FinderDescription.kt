package com.example.yourweather

import android.util.Log
import androidx.compose.ui.text.toLowerCase
import java.util.Locale

object FinderDescription {

    fun getDescription(descIn:String):String{
        val desc = descIn.lowercase(Locale.ROOT)
        Log.d("MyLog",desc)
        return when{
            "snow" in desc -> "Снег"
            "rain" in desc -> "Дождь"
            "Clear" in desc -> "ясно"
            "overcast" in desc -> "Облачно"
            "cloudy" in desc -> "Облачно"
            else -> {
                "Облачно"
            }
        }
    }
    fun getFoto(descIn:String):Int{
        val desc = descIn.toLowerCase()
        return when{
            "snow" in desc -> R.drawable.snow
            "rain" in desc -> R.drawable.rain
            "Clear" in desc -> R.drawable.sunny
            "overcast" in desc -> R.drawable.cloudy
            "cloudy" in desc -> R.drawable.cloudy
            else -> {
                R.drawable.cloudy
            }
        }
    }

}