package com.example.yourweather.models

sealed class DataLoadingState {
    object NeedLocation:DataLoadingState()
    object NeedWeatherForecast:DataLoadingState()
    object Loading : DataLoadingState()
    data class Success(val data: String) : DataLoadingState()
    data class Error(val message: String) : DataLoadingState()
}
