package com.example.yourweather.models

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class WeatherScreen(
    val current: Current,
    val forecast: Forecast
)
