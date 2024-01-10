package com.example.yourweather.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "last_weather")
data class WeatherForecast(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    @Embedded
    val location: Location,
    @Embedded
    val current: Current,
    @Embedded
    val forecast: Forecast
)

data class Location(
    val name: String,
    val tz_id: String
)

data class Current(
    val temp_c: Double,
    val last_updated_epoch: Long,
    val uv : Float,
    val gust_kph:Float,
    val humidity:Int
)

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val date_epoch: Long,
    @Embedded
    val condition: Condition,
    val hour: List<Hour>,
    @Embedded
    val day: Day
)

data class Hour(
    val time_epoch: Long,
    val temp_c: Double,
    @Embedded
    val condition: Condition
)

data class Condition(
    val text: String,
    val icon: String,
    val code: Int
)

data class Day(
    val maxtemp_c: Float,
    val mintemp_c: Float,
    val avghumidity: Int,
    @Embedded
    val condition: Condition
)
