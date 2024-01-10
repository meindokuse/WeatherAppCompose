package com.example.yourweather.models

sealed class AppAction{
    object RequestWeatherUpdate:AppAction()
    object RequestLocationUpdate:AppAction()
    object StartLoading : AppAction()
    object StopLoading : AppAction()
    data class WeatherUpdateSuccess(
        val weatherForecast: WeatherForecast):AppAction()


}
