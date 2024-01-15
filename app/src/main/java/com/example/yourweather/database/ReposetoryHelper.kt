package com.example.yourweather.database

import android.content.Context
import com.example.yourweather.models.Forecast
import com.example.yourweather.models.OtherLocations
import com.example.yourweather.models.WeatherForecast

class ReposetoryHelper(context: Context) {

    private val weatherDao = AppDatabase.getInstance(context).weatherDao()

    private val locationsDao = AppDatabase.getInstance(context).locationDao()

    suspend fun getAllWeatherForecasts(): WeatherForecast? {
        return weatherDao.getWeatherForecasts()
    }
    suspend fun insertWeatherForecast(weatherForecast: WeatherForecast) {
        weatherDao.insertWeatherForecast(weatherForecast)
    }
    suspend fun updateWeatherForecast(forecast: WeatherForecast){
        weatherDao.updateWeatherForecast(forecast)
    }

    suspend fun addNewLocation(location:OtherLocations){
        locationsDao.addNewLocation(location)
    }

    suspend fun getAllLocations():List<String>{
        val locations = mutableListOf<String>()
        locationsDao.getAllLocation().forEach{
            locations.add(it.location)
        }
       return locations
    }

    suspend fun deleteLocation(location: OtherLocations){
        locationsDao.deleteLocation(location)
    }
}