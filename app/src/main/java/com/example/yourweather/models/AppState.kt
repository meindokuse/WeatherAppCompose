package com.example.yourweather.models

sealed class AppState{
    object Idle : AppState()
    data class NoData(
        var loading: Boolean,
        var noLocation:Boolean,
        var noConnection:Boolean,
        var success:Boolean
    ):AppState()
    data class SuccessInit(
        val error:Int,
        val otherLocations: List<String>,
        val loading:Boolean,
        val weatherScreen: WeatherScreen,
        val location: String
    ) : AppState()

}
