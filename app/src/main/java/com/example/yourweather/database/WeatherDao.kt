package com.example.yourweather.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.yourweather.models.Current
import com.example.yourweather.models.Forecast
import com.example.yourweather.models.Location
import com.example.yourweather.models.WeatherForecast


@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherForecast(weatherForecast: WeatherForecast)

    @Query("SELECT * FROM last_weather")
    suspend fun getWeatherForecasts():WeatherForecast

    @Update
    suspend fun updateWeatherForecast(
        location: Location,
        current: Current,
        forecast: Forecast
    )
}